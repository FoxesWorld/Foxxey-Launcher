package org.foxesworld.launchserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.LauncherTrustManager;
import org.foxesworld.launcher.modules.events.PreConfigPhase;
import org.foxesworld.launcher.profiles.optional.actions.OptionalAction;
import org.foxesworld.launcher.profiles.optional.triggers.OptionalTrigger;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.GetAvailabilityAuthRequest;
import org.foxesworld.launchserver.auth.core.AuthCoreProvider;
import org.foxesworld.launchserver.auth.password.PasswordVerifier;
import org.foxesworld.launchserver.auth.protect.ProtectHandler;
import org.foxesworld.launchserver.auth.protect.hwid.HWIDProvider;
import org.foxesworld.launchserver.auth.session.SessionStorage;
import org.foxesworld.launchserver.auth.texture.TextureProvider;
import org.foxesworld.launchserver.components.Component;
import org.foxesworld.launchserver.config.LaunchServerConfig;
import org.foxesworld.launchserver.config.LaunchServerRuntimeConfig;
import org.foxesworld.launchserver.manangers.CertificateManager;
import org.foxesworld.launchserver.manangers.LaunchServerGsonManager;
import org.foxesworld.launchserver.modules.impl.LaunchServerModulesManager;
import org.foxesworld.launchserver.news.NewsProvider;
import org.foxesworld.launchserver.socket.WebSocketService;
import org.foxesworld.launchserver.client.ClientProfileProvider;
import org.foxesworld.utils.command.CommandHandler;
import org.foxesworld.utils.command.JLineCommandHandler;
import org.foxesworld.utils.command.StdCommandHandler;
import org.foxesworld.utils.helper.IOHelper;
import org.foxesworld.utils.helper.JVMHelper;
import org.foxesworld.utils.helper.LogHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.security.cert.CertificateException;

public class LaunchServerStarter {
    @SuppressWarnings("unused")
    public static final boolean allowUnsigned = Boolean.getBoolean("launchserver.allowUnsigned");
    public static final boolean prepareMode = Boolean.getBoolean("launchserver.prepareMode");
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        JVMHelper.checkStackTrace(LaunchServerStarter.class);
        JVMHelper.verifySystemProperties(LaunchServer.class, true);
        LogHelper.printVersion("LaunchServer");
        LogHelper.printLicense("LaunchServer");
        if (!StarterAgent.isAgentStarted()) {
            LogHelper.error("StarterAgent is not started!");
            LogHelper.error("You should add to JVM options this option: `-javaagent:LaunchServer.jar`");
        }
        Path dir = IOHelper.WORKING_DIR;
        Path configFile, runtimeConfigFile;
        try {
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            Security.addProvider(new BouncyCastleProvider());
        } catch (ClassNotFoundException ex) {
            LogHelper.error("Library BouncyCastle not found! Is directory 'libraries' empty?");
            return;
        }
        CertificateManager certificateManager = new CertificateManager();
        try {
            certificateManager.readTrustStore(dir.resolve("truststore"));
        } catch (CertificateException e) {
            throw new IOException(e);
        }
        {
            LauncherTrustManager.CheckClassResult result = certificateManager.checkClass(LaunchServer.class);
            if (result.type == LauncherTrustManager.CheckClassResultType.SUCCESS) {
                logger.info("LaunchServer signed by {}", result.endCertificate.getSubjectDN().getName());
            } else //noinspection StatementWithEmptyBody
                if (result.type == LauncherTrustManager.CheckClassResultType.NOT_SIGNED) {
                // None
            } else {
                if (result.exception != null) {
                    logger.error(result.exception);
                }
                logger.warn("LaunchServer signed incorrectly. Status: {}", result.type.name());
            }
        }

        LaunchServerRuntimeConfig runtimeConfig;
        LaunchServerConfig config;
        LaunchServer.LaunchServerEnv env = LaunchServer.LaunchServerEnv.PRODUCTION;
        LaunchServerModulesManager modulesManager = new LaunchServerModulesManager(dir.resolve("modules"), dir.resolve("config"), certificateManager.trustManager);
        modulesManager.autoload();
        modulesManager.initModules(null);
        registerAll();
        initGson(modulesManager);
        if (IOHelper.exists(dir.resolve("LaunchServer.conf"))) {
            configFile = dir.resolve("LaunchServer.conf");
        } else {
            configFile = dir.resolve("LaunchServer.json");
        }
        if (IOHelper.exists(dir.resolve("RuntimeLaunchServer.conf"))) {
            runtimeConfigFile = dir.resolve("RuntimeLaunchServer.conf");
        } else {
            runtimeConfigFile = dir.resolve("RuntimeLaunchServer.json");
        }
        CommandHandler localCommandHandler;
        try {
            Class.forName("org.jline.terminal.Terminal");

            // JLine2 available
            localCommandHandler = new JLineCommandHandler();
            logger.info("JLine2 terminal enabled");
        } catch (ClassNotFoundException ignored) {
            localCommandHandler = new StdCommandHandler(true);
            logger.warn("JLine2 isn't in classpath, using std");
        }
        modulesManager.invokeEvent(new PreConfigPhase());
        generateConfigIfNotExists(configFile, localCommandHandler, env);
        logger.info("Reading LaunchServer config file");
        try (BufferedReader reader = IOHelper.newReader(configFile)) {
            config = Launcher.gsonManager.gson.fromJson(reader, LaunchServerConfig.class);
        }
        if (!Files.exists(runtimeConfigFile)) {
            logger.info("Reset LaunchServer runtime config file");
            runtimeConfig = new LaunchServerRuntimeConfig();
            runtimeConfig.reset();
        } else {
            logger.info("Reading LaunchServer runtime config file");
            try (BufferedReader reader = IOHelper.newReader(runtimeConfigFile)) {
                runtimeConfig = Launcher.gsonManager.gson.fromJson(reader, LaunchServerRuntimeConfig.class);
            }
        }

        LaunchServer.LaunchServerConfigManager launchServerConfigManager = new LaunchServer.LaunchServerConfigManager() {
            @Override
            public LaunchServerConfig readConfig() throws IOException {
                LaunchServerConfig config1;
                try (BufferedReader reader = IOHelper.newReader(configFile)) {
                    config1 = Launcher.gsonManager.gson.fromJson(reader, LaunchServerConfig.class);
                }
                return config1;
            }

            @Override
            public LaunchServerRuntimeConfig readRuntimeConfig() throws IOException {
                LaunchServerRuntimeConfig config1;
                try (BufferedReader reader = IOHelper.newReader(runtimeConfigFile)) {
                    config1 = Launcher.gsonManager.gson.fromJson(reader, LaunchServerRuntimeConfig.class);
                }
                return config1;
            }

            @Override
            public void writeConfig(LaunchServerConfig config) throws IOException {
                try (Writer writer = IOHelper.newWriter(configFile)) {
                    if (Launcher.gsonManager.configGson != null) {
                        Launcher.gsonManager.configGson.toJson(config, writer);
                    } else {
                        logger.error("Error writing LaunchServer runtime config file. Gson is null");
                    }
                }
            }

            @Override
            public void writeRuntimeConfig(LaunchServerRuntimeConfig config) throws IOException {
                try (Writer writer = IOHelper.newWriter(runtimeConfigFile)) {
                    if (Launcher.gsonManager.configGson != null) {
                        Launcher.gsonManager.configGson.toJson(config, writer);
                    } else {
                        logger.error("Error writing LaunchServer runtime config file. Gson is null");
                    }
                }
            }
        };
        LaunchServer.LaunchServerDirectories directories = new LaunchServer.LaunchServerDirectories();
        directories.dir = dir;
        LaunchServer server = new LaunchServerBuilder()
                .setDirectories(directories)
                .setEnv(env)
                .setCommandHandler(localCommandHandler)
                .setRuntimeConfig(runtimeConfig)
                .setConfig(config)
                .setModulesManager(modulesManager)
                .setLaunchServerConfigManager(launchServerConfigManager)
                .setCertificateManager(certificateManager)
                .build();
        if (!prepareMode) {
            server.run();
        } else {
            server.close();
        }
    }

    public static void initGson(LaunchServerModulesManager modulesManager) {
        Launcher.gsonManager = new LaunchServerGsonManager(modulesManager);
        Launcher.gsonManager.initGson();
    }

    public static void registerAll() {
        AuthCoreProvider.registerProviders();
        PasswordVerifier.registerProviders();
        TextureProvider.registerProviders();
        Component.registerComponents();
        ProtectHandler.registerHandlers();
        ClientProfileProvider.registerProviders();
        NewsProvider.registerProviders();
        WebSocketService.registerResponses();
        AuthRequest.registerProviders();
        GetAvailabilityAuthRequest.registerProviders();
        HWIDProvider.registerProviders();
        OptionalAction.registerProviders();
        OptionalTrigger.registerProviders();
        SessionStorage.registerProviders();
    }

    public static void generateConfigIfNotExists(Path configFile, CommandHandler commandHandler, LaunchServer.LaunchServerEnv env) throws IOException {
        if (IOHelper.isFile(configFile))
            return;

        // Create new config
        logger.info("Creating LaunchServer config");

        LaunchServerConfig newConfig = LaunchServerConfig.getDefault(env);
        // Set server address
        String address;
        if (env.equals(LaunchServer.LaunchServerEnv.TEST)) {
            address = "localhost";
            newConfig.setProjectName("test");
        } else {
            address = System.getenv("ADDRESS");
            if (address == null) {
                address = System.getProperty("launchserver.address", null);
            }
            if (address == null) {
                System.out.println("LaunchServer address(default: localhost): ");
                address = commandHandler.readLine();
            }
            String projectName = System.getenv("PROJECTNAME");
            if (projectName == null) {
                projectName = System.getProperty("launchserver.projectname", null);
            }
            if (projectName == null) {
                System.out.println("LaunchServer projectName: ");
                projectName = commandHandler.readLine();
            }
            newConfig.setProjectName(projectName);
        }
        if (address == null || address.isEmpty()) {
            logger.error("Address null. Using localhost");
            address = "localhost";
        }
        if (newConfig.projectName == null || newConfig.projectName.isEmpty()) {
            logger.error("ProjectName null. Using MineCraft");
            newConfig.projectName = "MineCraft";
        }

        newConfig.netty.address = "ws://" + address + ":9274/api";
        newConfig.netty.downloadURL = "http://" + address + ":9274/%dirname%/";
        newConfig.netty.launcherURL = "http://" + address + ":9274/Launcher.jar";
        newConfig.netty.launcherEXEURL = "http://" + address + ":9274/Launcher.exe";

        // Write LaunchServer config
        logger.info("Writing LaunchServer config file");
        try (BufferedWriter writer = IOHelper.newWriter(configFile)) {
            Launcher.gsonManager.configGson.toJson(newConfig, writer);
        }
    }
}
