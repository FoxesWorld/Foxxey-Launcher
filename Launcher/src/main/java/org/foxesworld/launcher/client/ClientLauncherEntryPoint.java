package org.foxesworld.launcher.client;

import org.foxesworld.launcher.*;
import org.foxesworld.launcher.api.AuthService;
import org.foxesworld.launcher.api.ClientService;
import org.foxesworld.launcher.client.events.client.*;
import org.foxesworld.launcher.guard.LauncherGuardManager;
import org.foxesworld.launcher.hasher.FileNameMatcher;
import org.foxesworld.launcher.hasher.HashedDir;
import org.foxesworld.launcher.hasher.HashedEntry;
import org.foxesworld.launcher.managers.ClientGsonManager;
import org.foxesworld.launcher.managers.ConsoleManager;
import org.foxesworld.launcher.modules.events.PreConfigPhase;
import org.foxesworld.launcher.patches.FMLPatcher;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launcher.profiles.optional.actions.OptionalAction;
import org.foxesworld.launcher.profiles.optional.actions.OptionalActionClassPath;
import org.foxesworld.launcher.profiles.optional.actions.OptionalActionClientArgs;
import org.foxesworld.launcher.profiles.optional.triggers.OptionalTrigger;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.RequestException;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.GetAvailabilityAuthRequest;
import org.foxesworld.launcher.serialize.HInput;
import org.foxesworld.launcher.utils.DirWatcher;
import org.foxesworld.utils.helper.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientLauncherEntryPoint {
    private static ClassLoader classLoader;

    private static ClientLauncherProcess.ClientParams readParams(SocketAddress address) throws IOException {
        try (Socket socket = IOHelper.newSocket()) {
            socket.connect(address);
            try (HInput input = new HInput(socket.getInputStream())) {
                byte[] serialized = input.readByteArray(0);
                ClientLauncherProcess.ClientParams params = Launcher.gsonManager.gson.fromJson(new String(serialized, IOHelper.UNICODE_CHARSET), ClientLauncherProcess.ClientParams.class);
                params.clientHDir = new HashedDir(input);
                params.assetHDir = new HashedDir(input);
                boolean isNeedReadJavaDir = input.readBoolean();
                if (isNeedReadJavaDir)
                    params.javaHDir = new HashedDir(input);
                return params;
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        LauncherEngine.IS_CLIENT.set(true);
        LauncherEngine engine = LauncherEngine.clientInstance();
        JVMHelper.verifySystemProperties(ClientLauncherEntryPoint.class, true);
        EnvHelper.checkDangerousParams();
        JVMHelper.checkStackTrace(ClientLauncherEntryPoint.class);
        LogHelper.printVersion("Client Launcher");
        LauncherEngine.checkClass(LauncherEngine.class);
        LauncherEngine.checkClass(LauncherAgent.class);
        LauncherEngine.checkClass(ClientLauncherEntryPoint.class);
        LauncherEngine.modulesManager = new ClientModuleManager();
        LauncherEngine.modulesManager.loadModule(new ClientLauncherCoreModule());
        LauncherConfig.initModules(LauncherEngine.modulesManager); //INIT
        LauncherEngine.modulesManager.initModules(null);
        initGson(LauncherEngine.modulesManager);
        ConsoleManager.initConsole();
        LauncherEngine.modulesManager.invokeEvent(new PreConfigPhase());
        engine.readKeys();
        LauncherGuardManager.initGuard(true);
        LogHelper.debug("Reading ClientLauncher params");
        ClientLauncherProcess.ClientParams params = readParams(new InetSocketAddress("127.0.0.1", Launcher.getConfig().clientPort));
        if (params.profile.getClassLoaderConfig() != ClientProfile.ClassLoaderConfig.AGENT) {
            LauncherEngine.verifyNoAgent();
        }
        ClientProfile profile = params.profile;
        Launcher.profile = profile;
        AuthService.profile = profile;
        LauncherEngine.clientParams = params;
        if (params.oauth != null) {
            LogHelper.info("Using OAuth");
            if (params.oauthExpiredTime != 0) {
                Request.setOAuth(params.authId, params.oauth, params.oauthExpiredTime);
            } else {
                Request.setOAuth(params.authId, params.oauth);
            }
            if (params.extendedTokens != null) {
                Request.addAllExtendedToken(params.extendedTokens);
            }
        } else if (params.session != null) {
            LogHelper.info("Using Sessions");
            Request.setSession(params.session);
        }
        checkJVMBitsAndVersion(params.profile.getMinJavaVersion(), params.profile.getRecommendJavaVersion(), params.profile.getMaxJavaVersion(), params.profile.isWarnMissJavaVersion());
        LauncherEngine.modulesManager.invokeEvent(new ClientProcessInitPhase(engine, params));

        Path clientDir = Paths.get(params.clientDir);
        Path assetDir = Paths.get(params.assetDir);

        // Verify ClientLauncher sign and classpath
        LogHelper.debug("Verifying ClientLauncher sign and classpath");
        List<URL> classpath = resolveClassPath(clientDir, params.actions, params.profile).map(IOHelper::toURL).collect(Collectors.toList());
        // Start client with WatchService monitoring
        boolean digest = !profile.isUpdateFastCheck();
        LogHelper.debug("Restore sessions");
        Request.restore();
        Request.service.registerEventHandler(new BasicLauncherEventHandler());
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
        ClientProfile.ClassLoaderConfig classLoaderConfig = profile.getClassLoaderConfig();
        if (classLoaderConfig == ClientProfile.ClassLoaderConfig.LAUNCHER) {
            ClientClassLoader classLoader = new ClientClassLoader(classpath.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
            ClientLauncherEntryPoint.classLoader = classLoader;
            Thread.currentThread().setContextClassLoader(classLoader);
            classLoader.nativePath = clientDir.resolve("natives").toString();
            LauncherEngine.modulesManager.invokeEvent(new ClientProcessClassLoaderEvent(engine, classLoader, profile));
            AuthService.username = params.playerProfile.username;
            AuthService.uuid = params.playerProfile.uuid;
            ClientService.classLoader = classLoader;
            ClientService.nativePath = classLoader.nativePath;
            classLoader.addURL(IOHelper.getCodeSource(ClientLauncherEntryPoint.class).toUri().toURL());
            ClientService.baseURLs = classLoader.getURLs();
        } else if (classLoaderConfig == ClientProfile.ClassLoaderConfig.AGENT) {
            ClientLauncherEntryPoint.classLoader = ClassLoader.getSystemClassLoader();
            classpath.add(IOHelper.getCodeSource(ClientLauncherEntryPoint.class).toUri().toURL());
            for (URL url : classpath) {
                LauncherAgent.addJVMClassPath(Paths.get(url.toURI()));
            }
            ClientService.instrumentation = LauncherAgent.inst;
            ClientService.nativePath = clientDir.resolve("natives").toString();
            LauncherEngine.modulesManager.invokeEvent(new ClientProcessClassLoaderEvent(engine, classLoader, profile));
            ClientService.classLoader = classLoader;
            ClientService.baseURLs = classpath.toArray(new URL[0]);
        } else if (classLoaderConfig == ClientProfile.ClassLoaderConfig.SYSTEM_ARGS) {
            ClientLauncherEntryPoint.classLoader = ClassLoader.getSystemClassLoader();
            ClientService.classLoader = ClassLoader.getSystemClassLoader();
            ClientService.baseURLs = classpath.toArray(new URL[0]);
        }
        AuthService.username = params.playerProfile.username;
        AuthService.uuid = params.playerProfile.uuid;
        if (params.profile.getRuntimeInClientConfig() != ClientProfile.RuntimeInClientConfig.NONE) {
            CommonHelper.newThread("Client Launcher Thread", true, () -> {
                try {
                    engine.start(args);
                } catch (Throwable throwable) {
                    LogHelper.error(throwable);
                }
            }).start();
        }
        LauncherEngine.modulesManager.invokeEvent(new ClientProcessReadyEvent(engine, params));
        LogHelper.debug("Starting JVM and client WatchService");
        FileNameMatcher assetMatcher = profile.getAssetUpdateMatcher();
        FileNameMatcher clientMatcher = profile.getClientUpdateMatcher();
        Path javaDir = Paths.get(System.getProperty("java.home"));
        try (DirWatcher assetWatcher = new DirWatcher(assetDir, params.assetHDir, assetMatcher, digest);
             DirWatcher clientWatcher = new DirWatcher(clientDir, params.clientHDir, clientMatcher, digest);
             DirWatcher javaWatcher = params.javaHDir == null ? null : new DirWatcher(javaDir, params.javaHDir, null, digest)) {
            // Verify current state of all dirs
            //verifyHDir(IOHelper.JVM_DIR, jvmHDir.object, null, digest);
            //for (OptionalFile s : Launcher.profile.getOptional()) {
            //    if (params.updateOptional.contains(s)) s.mark = true;
            //    else hdir.removeR(s.file);
            //}
            // Start WatchService, and only then client
            CommonHelper.newThread("Asset Directory Watcher", true, assetWatcher).start();
            CommonHelper.newThread("Client Directory Watcher", true, clientWatcher).start();
            if (javaWatcher != null)
                CommonHelper.newThread("Java Directory Watcher", true, clientWatcher).start();
            verifyHDir(assetDir, params.assetHDir, assetMatcher, digest);
            verifyHDir(clientDir, params.clientHDir, clientMatcher, digest);
            if (javaWatcher != null)
                verifyHDir(javaDir, params.javaHDir, null, digest);
            LauncherEngine.modulesManager.invokeEvent(new ClientProcessLaunchEvent(engine, params));
            launch(profile, params);
        }
    }

    private static void initGson(ClientModuleManager moduleManager) {
        AuthRequest.registerProviders();
        GetAvailabilityAuthRequest.registerProviders();
        OptionalAction.registerProviders();
        OptionalTrigger.registerProviders();
        Launcher.gsonManager = new ClientGsonManager(moduleManager);
        Launcher.gsonManager.initGson();
    }

    public static void verifyHDir(Path dir, HashedDir hdir, FileNameMatcher matcher, boolean digest) throws IOException {
        //if (matcher != null)
        //    matcher = matcher.verifyOnly();

        // Hash directory and compare (ignore update-only matcher entries, it will break offline-mode)
        HashedDir currentHDir = new HashedDir(dir, matcher, true, digest);
        HashedDir.Diff diff = hdir.diff(currentHDir, matcher);
        if (!diff.isSame()) {
            if (LogHelper.isDebugEnabled()) {
                diff.extra.walk(File.separator, (e, k, v) -> {
                    if (v.getType().equals(HashedEntry.Type.FILE)) {
                        LogHelper.error("Extra file %s", e);
                    } else LogHelper.error("Extra %s", e);
                    return HashedDir.WalkAction.CONTINUE;
                });
                diff.mismatch.walk(File.separator, (e, k, v) -> {
                    if (v.getType().equals(HashedEntry.Type.FILE)) {
                        LogHelper.error("Mismatch file %s", e);
                    } else LogHelper.error("Mismatch %s", e);
                    return HashedDir.WalkAction.CONTINUE;
                });
            }
            throw new SecurityException(String.format("Forbidden modification: '%s'", IOHelper.getFileName(dir)));
        }
    }

    public static boolean checkJVMBitsAndVersion(int minVersion, int recommendVersion, int maxVersion, boolean showMessage) {
        boolean ok = true;
        if (JVMHelper.JVM_BITS != JVMHelper.OS_BITS) {
            String error = String.format("У Вас установлена Java %d, но Ваша система определена как %d. Установите Java правильной разрядности", JVMHelper.JVM_BITS, JVMHelper.OS_BITS);
            LogHelper.error(error);
            if (showMessage)
                JOptionPane.showMessageDialog(null, error);
            ok = false;
        }
        String jvmVersion = JVMHelper.RUNTIME_MXBEAN.getVmVersion();
        LogHelper.info(jvmVersion);
        int version = JVMHelper.getVersion();
        if (version < minVersion || version > maxVersion) {
            String error = String.format("У Вас установлена Java %s. Для правильной работы необходима Java %d", JVMHelper.RUNTIME_MXBEAN.getVmVersion(), recommendVersion);
            LogHelper.error(error);
            if (showMessage)
                JOptionPane.showMessageDialog(null, error);
            ok = false;
        }
        return ok;
    }

    private static LinkedList<Path> resolveClassPathList(Path clientDir, String... classPath) throws IOException {
        return resolveClassPathStream(clientDir, classPath).collect(Collectors.toCollection(LinkedList::new));
    }

    private static Stream<Path> resolveClassPathStream(Path clientDir, String... classPath) throws IOException {
        Stream.Builder<Path> builder = Stream.builder();
        for (String classPathEntry : classPath) {
            Path path = clientDir.resolve(IOHelper.toPath(classPathEntry.replace(IOHelper.CROSS_SEPARATOR, IOHelper.PLATFORM_SEPARATOR)));
            if (IOHelper.isDir(path)) { // Recursive walking and adding
                IOHelper.walk(path, new ClassPathFileVisitor(builder), false);
                continue;
            }
            builder.accept(path);
        }
        return builder.build();
    }

    public static Stream<Path> resolveClassPath(Path clientDir, Set<OptionalAction> actions, ClientProfile profile) throws IOException {
        Stream<Path> result = resolveClassPathStream(clientDir, profile.getClassPath());
        for (OptionalAction a : actions) {
            if (a instanceof OptionalActionClassPath)
                result = Stream.concat(result, resolveClassPathStream(clientDir, ((OptionalActionClassPath) a).args));
        }
        return result;
    }

    private static void launch(ClientProfile profile, ClientLauncherProcess.ClientParams params) throws Throwable {
        // Add client args
        Collection<String> args = new LinkedList<>();
        if (profile.getVersion().compareTo(ClientProfile.Version.MC164) >= 0)
            params.addClientArgs(args);
        else {
            params.addClientLegacyArgs(args);
            System.setProperty("minecraft.applet.TargetDirectory", params.clientDir);
        }
        Collections.addAll(args, profile.getClientArgs());
        for (OptionalAction action : params.actions) {
            if (action instanceof OptionalActionClientArgs) {
                args.addAll(((OptionalActionClientArgs) action).args);
            }
        }
        List<String> copy = new ArrayList<>(args);
        for (int i = 0, l = copy.size(); i < l; i++) {
            String s = copy.get(i);
            if (i + 1 < l && ("--accessToken".equals(s) || "--session".equals(s))) {
                copy.set(i + 1, "censored");
            }
        }
        LogHelper.debug("Args: " + copy);
        // Resolve main class and method
        Class<?> mainClass = classLoader.loadClass(profile.getMainClass());
        if (LogHelper.isDevEnabled() && classLoader instanceof URLClassLoader) {
            for (URL u : ((URLClassLoader) classLoader).getURLs()) {
                LogHelper.dev("ClassLoader URL: %s", u.toString());
            }
        }
        FMLPatcher.apply();
        LauncherEngine.modulesManager.invokeEvent(new ClientProcessPreInvokeMainClassEvent(params, profile, args));
        {
            List<String> compatClasses = profile.getCompatClasses();
            for (String e : compatClasses) {
                Class<?> clazz = classLoader.loadClass(e);
                MethodHandle runMethod = MethodHandles.publicLookup().findStatic(clazz, "run", MethodType.methodType(void.class));
                runMethod.invoke();
            }
        }
        MethodHandle mainMethod = MethodHandles.publicLookup().findStatic(mainClass, "main", MethodType.methodType(void.class, String[].class)).asFixedArity();
        Launcher.LAUNCHED.set(true);
        JVMHelper.fullGC();
        // Invoke main method
        try {
            mainMethod.invokeWithArguments((Object) args.toArray(new String[0]));
            LogHelper.debug("Main exit successful");
        } catch (Throwable e) {
            LogHelper.error(e);
            throw e;
        } finally {
            LauncherEngine.exitLauncher(0);
        }

    }

    private static final class ClassPathFileVisitor extends SimpleFileVisitor<Path> {
        private final Stream.Builder<Path> result;

        private ClassPathFileVisitor(Stream.Builder<Path> result) {
            this.result = result;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (IOHelper.hasExtension(file, "jar") || IOHelper.hasExtension(file, "zip"))
                result.accept(file);
            return super.visitFile(file, attrs);
        }
    }
}
