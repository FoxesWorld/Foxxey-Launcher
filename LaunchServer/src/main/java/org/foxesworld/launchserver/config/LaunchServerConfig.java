package org.foxesworld.launchserver.config;

import io.netty.channel.epoll.Epoll;
import io.netty.handler.logging.LogLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.LauncherConfig;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.auth.core.RejectAuthCoreProvider;
import org.foxesworld.launchserver.auth.protect.ProtectHandler;
import org.foxesworld.launchserver.auth.protect.StdProtectHandler;
import org.foxesworld.launchserver.auth.session.MemorySessionStorage;
import org.foxesworld.launchserver.auth.session.SessionStorage;
import org.foxesworld.launchserver.auth.texture.RequestTextureProvider;
import org.foxesworld.launchserver.binary.tasks.exe.Launch4JTask;
import org.foxesworld.launchserver.components.AuthLimiterComponent;
import org.foxesworld.launchserver.components.Component;
import org.foxesworld.launchserver.components.ProGuardComponent;
import org.foxesworld.launchserver.components.RegLimiterComponent;
import org.foxesworld.launchserver.dao.provider.DaoProvider;
import org.foxesworld.launchserver.client.ClientProfileProvider;
import org.foxesworld.launchserver.client.MysqlClientProfileProvider;
import org.foxesworld.launchserver.news.EmptyNewsProvider;
import org.foxesworld.launchserver.news.NewsProvider;
import org.foxesworld.utils.Version;
import org.foxesworld.utils.helper.JVMHelper;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class LaunchServerConfig {
    private transient final Logger logger = LogManager.getLogger();
    public String projectName;
    public String[] mirrors;
    public String binaryName;
    public boolean copyBinaries = true;
    public boolean cacheUpdates = true;
    public LauncherConfig.LauncherEnvironment env;
    public Map<String, AuthProviderPair> auth;
    public DaoProvider dao;
    public SessionStorage sessions;
    // Handlers & Providers
    public ProtectHandler protectHandler;
    public Map<String, Component> components;
    public ExeConf launch4j;
    public NettyConfig netty;
    public LauncherConf launcher;
    public JarSignerConf sign;
    public String startScript;
    public NewsProvider newsProvider;
    public ClientProfileProvider clientProfileProvider;
    private transient LaunchServer server = null;
    private transient AuthProviderPair authDefault;

    public static LaunchServerConfig getDefault(@SuppressWarnings("unused") LaunchServer.LaunchServerEnv env) {
        LaunchServerConfig newConfig = new LaunchServerConfig();
        newConfig.mirrors = new String[]{"https://mirror.foxesworld.ru/"};
        newConfig.launch4j = new LaunchServerConfig.ExeConf();
        newConfig.launch4j.enabled = true;
        newConfig.launch4j.copyright = "© FoxesWorld Team";
        newConfig.launch4j.fileDesc = "FoxesLauncher ".concat(Version.getVersion().getVersionString());
        newConfig.launch4j.fileVer = Version.getVersion().getVersionString().concat(".").concat(String.valueOf(Version.getVersion().patch));
        newConfig.launch4j.internalName = "Launcher";
        newConfig.launch4j.trademarks = "This product is licensed under GNU GPLv3";
        newConfig.launch4j.txtFileVersion = "%s, build %d";
        newConfig.launch4j.txtProductVersion = "%s, build %d";
        newConfig.launch4j.productName = "FoxesLauncher";
        newConfig.launch4j.productVer = newConfig.launch4j.fileVer;
        newConfig.launch4j.maxVersion = "1.8.999";
        newConfig.env = LauncherConfig.LauncherEnvironment.STD;
        newConfig.startScript = JVMHelper.OS_TYPE.equals(JVMHelper.OS.MUSTDIE) ? "." + File.separator + "start.bat" : "." + File.separator + "start.sh";
        newConfig.auth = new HashMap<>();
        AuthProviderPair a = new AuthProviderPair(new RejectAuthCoreProvider(),
                new RequestTextureProvider("http://example.com/skins/%username%.png", "http://example.com/cloaks/%username%.png")
        );
        a.displayName = "Default";
        newConfig.auth.put("std", a);
        newConfig.protectHandler = new StdProtectHandler();
        newConfig.clientProfileProvider = MysqlClientProfileProvider.createDefault();
        newConfig.newsProvider = new EmptyNewsProvider();
        newConfig.sessions = new MemorySessionStorage();
        newConfig.binaryName = "Launcher";

        newConfig.netty = new NettyConfig();
        newConfig.netty.fileServerEnabled = true;
        newConfig.netty.sendExceptionEnabled = false;
        newConfig.netty.binds = new NettyBindAddress[]{new NettyBindAddress("0.0.0.0", 9274)};
        newConfig.netty.performance = new NettyPerformanceConfig();
        try {
            newConfig.netty.performance.usingEpoll = Epoll.isAvailable();
        } catch (Throwable e) {
            // Epoll class line 51+ catch (Exception) but Error will be thrown by System.load
            newConfig.netty.performance.usingEpoll = false;
        } // such as on ARM
        newConfig.netty.performance.bossThread = 2;
        newConfig.netty.performance.workerThread = 8;
        newConfig.netty.performance.schedulerThread = 2;

        newConfig.launcher = new LauncherConf();
        newConfig.launcher.guardType = "no";
        newConfig.launcher.compress = true;
        newConfig.launcher.deleteTempFiles = true;
        newConfig.launcher.stripLineNumbers = true;

        newConfig.sign = new JarSignerConf();

        newConfig.components = new HashMap<>();
        AuthLimiterComponent authLimiterComponent = new AuthLimiterComponent();
        authLimiterComponent.rateLimit = 3;
        authLimiterComponent.rateLimitMillis = 8000;
        authLimiterComponent.message = "Превышен лимит авторизаций";
        newConfig.components.put("authLimiter", authLimiterComponent);
        RegLimiterComponent regLimiterComponent = new RegLimiterComponent();
        regLimiterComponent.rateLimit = 3;
        regLimiterComponent.rateLimitMillis = 1000 * 60 * 60 * 10; //Блок на 10 часов
        regLimiterComponent.message = "Превышен лимит регистраций";
        newConfig.components.put("regLimiter", regLimiterComponent);
        ProGuardComponent proGuardComponent = new ProGuardComponent();
        newConfig.components.put("proguard", proGuardComponent);
        return newConfig;
    }

    public LaunchServerConfig setLaunchServer(LaunchServer server) {
        this.server = server;
        return this;
    }

    public AuthProviderPair getAuthProviderPair(String name) {
        return auth.get(name);
    }

    public AuthProviderPair getAuthProviderPair() {
        if (authDefault != null) return authDefault;
        for (AuthProviderPair pair : auth.values()) {
            if (pair.isDefault) {
                authDefault = pair;
                return pair;
            }
        }
        throw new IllegalStateException("Default AuthProviderPair not found");
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @SuppressWarnings("unused")
    public void setBinaryName(String binaryName) {
        this.binaryName = binaryName;
    }

    @SuppressWarnings("unused")
    public void setEnv(LauncherConfig.LauncherEnvironment env) {
        this.env = env;
    }

    public void verify() {
        if (auth == null || auth.size() < 1) {
            throw new NullPointerException("AuthProviderPair`s count should be at least one");
        }

        if (dao != null) {
            logger.warn("DAO deprecated and may be remove in future release");
        }

        boolean isOneDefault = false;
        for (AuthProviderPair pair : auth.values()) {
            if (pair.isDefault) {
                isOneDefault = true;
                break;
            }
        }
        verifyNonNull(clientProfileProvider, "ClientProfileProvider");
        verifyNonNull(newsProvider, "NewsProvider");
        verifyNonNull(protectHandler, "ProtectHandler");
        if (!isOneDefault) {
            throw new IllegalStateException("No auth pairs declared by default.");
        }
        verifyNonNull(env, "Env");
        verifyNonNull(netty, "Netty");
    }

    private void verifyNonNull(Object object, String objectName) {
        if (object == null) {
            throw new NullPointerException(objectName + " must not be null");
        }
    }

    public void init(LaunchServer.ReloadType type) {
        Launcher.applyLauncherEnv(env);
        for (Map.Entry<String, AuthProviderPair> provider : auth.entrySet()) {
            provider.getValue().init(server, provider.getKey());
            if (!provider.getValue().isUseCore()) {
                logger.warn("Deprecated auth {}: legacy provider/handler auth may be removed in future release", provider.getKey());
            }
        }
        if (dao != null) {
            server.registerObject("dao", dao);
            dao.init(server);
        }
        if (protectHandler != null) {
            server.registerObject("protectHandler", protectHandler);
            protectHandler.init(server);
            protectHandler.checkLaunchServerLicense();
        }
        if (clientProfileProvider != null) {
            server.registerObject("clientProfileProvider", clientProfileProvider);
            clientProfileProvider.init(server);
        }
        if (newsProvider != null) {
            server.registerObject("newsProvider", newsProvider);
            newsProvider.init(server);
        }
        if (sessions != null) {
            sessions.init(server);
            server.registerObject("sessions", sessions);
        }
        if (components != null) {
            components.forEach((k, v) -> server.registerObject("component.".concat(k), v));
        }
        if (!type.equals(LaunchServer.ReloadType.NO_AUTH)) {
            for (AuthProviderPair pair : auth.values()) {
                server.registerObject("auth.".concat(pair.name).concat(".provider"), pair.provider);
                server.registerObject("auth.".concat(pair.name).concat(".handler"), pair.handler);
                server.registerObject("auth.".concat(pair.name).concat(".core"), pair.core);
                server.registerObject("auth.".concat(pair.name).concat(".social"), pair.social);
                server.registerObject("auth.".concat(pair.name).concat(".texture"), pair.textureProvider);
            }
        }
        Arrays.stream(mirrors).forEach(server.mirrorManager::addMirror);
    }

    public void close(LaunchServer.ReloadType type) {
        try {
            if (!type.equals(LaunchServer.ReloadType.NO_AUTH)) {
                for (AuthProviderPair pair : auth.values()) {
                    server.unregisterObject("auth.".concat(pair.name).concat(".provider"), pair.provider);
                    server.unregisterObject("auth.".concat(pair.name).concat(".handler"), pair.handler);
                    server.unregisterObject("auth.".concat(pair.name).concat(".social"), pair.social);
                    server.unregisterObject("auth.".concat(pair.name).concat(".core"), pair.core);
                    server.unregisterObject("auth.".concat(pair.name).concat(".texture"), pair.textureProvider);
                    pair.close();
                }
            }
            if (type.equals(LaunchServer.ReloadType.FULL)) {
                components.forEach((k, component) -> {
                    server.unregisterObject("component.".concat(k), component);
                    if (component instanceof AutoCloseable) {
                        try {
                            ((AutoCloseable) component).close();
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            logger.error(e);
        }
        if (protectHandler != null) {
            server.unregisterObject("protectHandler", protectHandler);
            protectHandler.close();
        }
        if (clientProfileProvider != null) {
            server.unregisterObject("clientProfileProvider", clientProfileProvider);
            clientProfileProvider.close();
        }
        if (newsProvider != null) {
            server.unregisterObject("newsProvider", newsProvider);
            newsProvider.close();
        }
        if (sessions != null) {
            server.unregisterObject("sessions", sessions);
            if (sessions instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) sessions).close();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        if (dao != null) {
            server.unregisterObject("dao", dao);
            if (dao instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) dao).close();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
    }

    public static class ExeConf {
        public boolean enabled;
        public boolean setMaxVersion;
        public String maxVersion;
        public String minVersion = "1.8.0";
        public String supportURL = null;
        public String downloadUrl = Launch4JTask.DOWNLOAD_URL;
        public String productName;
        public String productVer;
        public String fileDesc;
        public String fileVer;
        public String internalName;
        public String copyright;
        public String trademarks;

        public String txtFileVersion;
        public String txtProductVersion;
    }

    public static class JarSignerConf {
        public boolean enabled = false;
        public String keyStore = "pathToKey";
        public String keyStoreType = "JKS";
        public String keyStorePass = "mypass";
        public String keyAlias = "myname";
        public String keyPass = "mypass";
        public String metaInfKeyName = "SIGNUMO.RSA";
        public String metaInfSfName = "SIGNUMO.SF";
        public String signAlgo = "SHA256WITHRSA";
    }

    public static class NettyUpdatesBind {
        public String url;
        public boolean zip;
    }

    public static class LauncherConf {
        public String guardType;
        public boolean compress;
        @Deprecated
        public boolean warningMissArchJava;
        public boolean stripLineNumbers;
        public boolean deleteTempFiles;
        public boolean certificatePinning;
        public boolean encryptRuntime;
        public int memoryLimit = 256;
    }

    public static class NettyConfig {
        public boolean fileServerEnabled;
        public boolean sendExceptionEnabled;
        public boolean ipForwarding;
        public boolean disableWebApiInterface;
        public boolean showHiddenFiles;
        public String launcherURL;
        public String downloadURL;
        public String launcherEXEURL;
        public String address;
        public Map<String, LaunchServerConfig.NettyUpdatesBind> bindings = new HashMap<>();
        public NettyPerformanceConfig performance;
        public NettyBindAddress[] binds;
        public LogLevel logLevel = LogLevel.DEBUG;
    }

    public static class NettyPerformanceConfig {
        public boolean usingEpoll;
        public int bossThread;
        public int workerThread;
        public int schedulerThread;
        public long sessionLifetimeMs = 24 * 60 * 60 * 1000;
        public int maxWebSocketRequestBytes = 1024 * 1024;
    }

    public static class NettyBindAddress {
        public String address;
        public int port;

        public NettyBindAddress(String address, int port) {
            this.address = address;
            this.port = port;
        }
    }
}
