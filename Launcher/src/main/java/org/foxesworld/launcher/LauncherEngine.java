package org.foxesworld.launcher;

import org.foxesworld.launcher.client.*;
import org.foxesworld.launcher.client.events.ClientEngineInitPhase;
import org.foxesworld.launcher.client.events.ClientExitPhase;
import org.foxesworld.launcher.client.events.ClientPreGuiPhase;
import org.foxesworld.launcher.console.GetPublicKeyCommand;
import org.foxesworld.launcher.console.SignDataCommand;
import org.foxesworld.launcher.guard.LauncherGuardInterface;
import org.foxesworld.launcher.guard.LauncherGuardManager;
import org.foxesworld.launcher.guard.LauncherNoGuard;
import org.foxesworld.launcher.guard.LauncherWrapperGuard;
import org.foxesworld.launcher.gui.NoRuntimeProvider;
import org.foxesworld.launcher.gui.RuntimeProvider;
import org.foxesworld.launcher.managers.ClientGsonManager;
import org.foxesworld.launcher.managers.ConsoleManager;
import org.foxesworld.launcher.modules.events.PreConfigPhase;
import org.foxesworld.launcher.profiles.optional.actions.OptionalAction;
import org.foxesworld.launcher.profiles.optional.triggers.OptionalTrigger;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.RequestException;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.GetAvailabilityAuthRequest;
import org.foxesworld.launcher.request.websockets.StdWebSocketService;
import org.foxesworld.launcher.utils.NativeJVMHalt;
import org.foxesworld.utils.helper.*;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class LauncherEngine {
    public static final AtomicBoolean IS_CLIENT = new AtomicBoolean(false);
    public static ClientLauncherProcess.ClientParams clientParams;
    public static LauncherGuardInterface guard;
    public static ClientModuleManager modulesManager;
    public final boolean clientInstance;
    // Instance
    private final AtomicBoolean started = new AtomicBoolean(false);
    public RuntimeProvider runtimeProvider;
    public ECPublicKey publicKey;
    public ECPrivateKey privateKey;

    private LauncherEngine(boolean clientInstance) {

        this.clientInstance = clientInstance;
    }

    //JVMHelper.getCertificates
    public static X509Certificate[] getCertificates(Class<?> clazz) {
        Object[] signers = clazz.getSigners();
        if (signers == null) return null;
        return Arrays.stream(signers).filter((c) -> c instanceof X509Certificate).map((c) -> (X509Certificate) c).toArray(X509Certificate[]::new);
    }

    public static void checkClass(Class<?> clazz) throws SecurityException {
        LauncherTrustManager trustManager = Launcher.getConfig().trustManager;
        if (trustManager == null) return;
        X509Certificate[] certificates = getCertificates(clazz);
        if (certificates == null) {
            throw new SecurityException(String.format("Class %s not signed", clazz.getName()));
        }
        try {
            trustManager.checkCertificatesSuccess(certificates, trustManager::stdCertificateChecker);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }

    public static void exitLauncher(int code) {
        modulesManager.invokeEvent(new ClientExitPhase(code));
        try {
            System.exit(code);
        } catch (Throwable e) //Forge Security Manager?
        {
            NativeJVMHalt.haltA(code);
        }
    }

    public static void main(String... args) throws Throwable {
        JVMHelper.checkStackTrace(LauncherEngine.class);
        JVMHelper.verifySystemProperties(Launcher.class, true);
        EnvHelper.checkDangerousParams();
        //if(!LauncherAgent.isStarted()) throw new SecurityException("JavaAgent not set");
        verifyNoAgent();
        LogHelper.printVersion("Launcher");
        LogHelper.printLicense("Launcher");
        LauncherEngine.checkClass(LauncherEngine.class);
        LauncherEngine.checkClass(LauncherAgent.class);
        LauncherEngine.checkClass(ClientLauncherEntryPoint.class);
        LauncherEngine.modulesManager = new ClientModuleManager();
        LauncherEngine.modulesManager.loadModule(new ClientLauncherCoreModule());
        LauncherConfig.initModules(LauncherEngine.modulesManager);
        LauncherEngine.modulesManager.initModules(null);
        // Start Launcher
        initGson(LauncherEngine.modulesManager);
        ConsoleManager.initConsole();
        LauncherEngine.modulesManager.invokeEvent(new PreConfigPhase());
        Launcher.getConfig(); // init config
        long startTime = System.currentTimeMillis();
        try {
            new LauncherEngine(false).start(args);
        } catch (Exception e) {
            LogHelper.error(e);
            return;
        }
        long endTime = System.currentTimeMillis();
        LogHelper.debug("Launcher started in %dms", endTime - startTime);
        //Request.service.close();
        //FunctionalBridge.close();
        LauncherEngine.exitLauncher(0);
    }

    public static void initGson(ClientModuleManager modulesManager) {
        AuthRequest.registerProviders();
        GetAvailabilityAuthRequest.registerProviders();
        OptionalAction.registerProviders();
        OptionalTrigger.registerProviders();
        Launcher.gsonManager = new ClientGsonManager(modulesManager);
        Launcher.gsonManager.initGson();
    }

    public static void verifyNoAgent() {
        if (JVMHelper.RUNTIME_MXBEAN.getInputArguments().stream().filter(e -> e != null && !e.isEmpty()).anyMatch(e -> e.contains("javaagent")))
            throw new SecurityException("JavaAgent found");
    }

    public ECPublicKey getClientPublicKey() {
        return publicKey;
    }

    public byte[] sign(byte[] bytes) {
        return SecurityHelper.sign(bytes, privateKey);
    }

    public static LauncherGuardInterface tryGetStdGuard() {
        switch (Launcher.getConfig().guardType) {
            case "no":
                return new LauncherNoGuard();
            case "wrapper":
                return new LauncherWrapperGuard();
        }
        return null;
    }

    public static LauncherEngine clientInstance() {
        return new LauncherEngine(true);
    }

    public static LauncherEngine newInstance(boolean clientInstance) {
        return new LauncherEngine(clientInstance);
    }

    public void readKeys() throws IOException, InvalidKeySpecException {
        if (privateKey != null || publicKey != null) return;
        Path dir = DirBridge.dir;
        Path publicKeyFile = dir.resolve("public.key");
        Path privateKeyFile = dir.resolve("private.key");
        if (IOHelper.isFile(publicKeyFile) && IOHelper.isFile(privateKeyFile)) {
            LogHelper.info("Reading EC keypair");
            publicKey = SecurityHelper.toPublicECDSAKey(IOHelper.read(publicKeyFile));
            privateKey = SecurityHelper.toPrivateECDSAKey(IOHelper.read(privateKeyFile));
        } else {
            LogHelper.info("Generating EC keypair");
            KeyPair pair = SecurityHelper.genECDSAKeyPair(new SecureRandom());
            publicKey = (ECPublicKey) pair.getPublic();
            privateKey = (ECPrivateKey) pair.getPrivate();

            // Write key pair list
            LogHelper.info("Writing EC keypair list");
            IOHelper.write(publicKeyFile, publicKey.getEncoded());
            IOHelper.write(privateKeyFile, privateKey.getEncoded());
        }
    }

    public void start(String... args) throws Throwable {
        //Launcher.modulesManager = new ClientModuleManager(this);
        LauncherEngine.guard = tryGetStdGuard();
        ClientPreGuiPhase event = new ClientPreGuiPhase(null);
        LauncherEngine.modulesManager.invokeEvent(event);
        runtimeProvider = event.runtimeProvider;
        if (runtimeProvider == null) runtimeProvider = new NoRuntimeProvider();
        runtimeProvider.init(clientInstance);
        //runtimeProvider.preLoad();
        if (Request.service == null) {
            String address = Launcher.getConfig().address;
            LogHelper.debug("Start async connection to %s", address);
            Request.service = StdWebSocketService.initWebSockets(address, true);
            Request.service.reconnectCallback = () ->
            {
                LogHelper.debug("WebSocket connect closed. Try reconnect");
                try {
                    Request.reconnect();
                } catch (Exception e) {
                    LogHelper.error(e);
                    throw new RequestException("Connection failed", e);
                }
            };
            Request.service.registerEventHandler(new BasicLauncherEventHandler());
        }
        Objects.requireNonNull(args, "args");
        if (started.getAndSet(true))
            throw new IllegalStateException("Launcher has been already started");
        readKeys();
        registerCommands();
        LauncherEngine.modulesManager.invokeEvent(new ClientEngineInitPhase(this));
        runtimeProvider.preLoad();
        LauncherGuardManager.initGuard(clientInstance);
        LogHelper.debug("Dir: %s", DirBridge.dir);
        runtimeProvider.run(args);
    }

    private void registerCommands() {
        ConsoleManager.handler.registerCommand("getpublickey", new GetPublicKeyCommand(this));
        ConsoleManager.handler.registerCommand("signdata", new SignDataCommand(this));
    }
}
