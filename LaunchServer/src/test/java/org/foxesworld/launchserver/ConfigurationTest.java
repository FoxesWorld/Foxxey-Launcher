package org.foxesworld.launchserver;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.password.AuthPlainPassword;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.auth.handler.MemoryAuthHandler;
import org.foxesworld.launchserver.auth.provider.AuthProvider;
import org.foxesworld.launchserver.auth.provider.AuthProviderResult;
import org.foxesworld.launchserver.auth.texture.NullTextureProvider;
import org.foxesworld.launchserver.config.LaunchServerConfig;
import org.foxesworld.launchserver.config.LaunchServerRuntimeConfig;
import org.foxesworld.launchserver.impl.TestLaunchServerConfigManager;
import org.foxesworld.launchserver.manangers.CertificateManager;
import org.foxesworld.launchserver.manangers.LaunchServerGsonManager;
import org.foxesworld.launchserver.modules.impl.LaunchServerModulesManager;
import org.foxesworld.utils.command.StdCommandHandler;
import org.foxesworld.utils.helper.SecurityHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;

public class ConfigurationTest {
    @TempDir
    public static Path modulesDir;
    @TempDir
    public static Path configDir;
    @TempDir
    public static Path dir;
    public static LaunchServer launchServer;
    public static TestLaunchServerConfigManager launchServerConfigManager;

    @BeforeAll
    public static void prepare() throws Throwable {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
        LaunchServerModulesManager modulesManager = new LaunchServerModulesManager(modulesDir, configDir, null);
        LaunchServerConfig config = LaunchServerConfig.getDefault(LaunchServer.LaunchServerEnv.TEST);
        Launcher.gsonManager = new LaunchServerGsonManager(modulesManager);
        Launcher.gsonManager.initGson();
        LaunchServerRuntimeConfig runtimeConfig = new LaunchServerRuntimeConfig();
        LaunchServerBuilder builder = new LaunchServerBuilder();
        launchServerConfigManager = new TestLaunchServerConfigManager();
        builder.setDir(dir)
                .setEnv(LaunchServer.LaunchServerEnv.TEST)
                .setConfig(config)
                .setRuntimeConfig(runtimeConfig)
                .setCertificateManager(new CertificateManager())
                .setLaunchServerConfigManager(launchServerConfigManager)
                .setModulesManager(modulesManager)
                .setCommandHandler(new StdCommandHandler(false));
        launchServer = builder.build();
    }

    @Test
    public void reloadTest() throws Exception {
        AuthProvider provider = new AuthProvider() {
            @Override
            public AuthProviderResult auth(String login, AuthRequest.AuthPasswordInterface password, String ip, String hwid) throws Exception {
                if (!(password instanceof AuthPlainPassword)) throw new UnsupportedOperationException();
                if (login.equals("test") && ((AuthPlainPassword) password).password.equals("test")) {
                    return new AuthProviderResult(login, SecurityHelper.randomStringToken(), new ClientPermissions(), 0, 4);
                }
                throw new AuthException("Incorrect password");
            }

            @Override
            public void close() throws IOException {

            }
        };
        AuthProviderPair pair = new AuthProviderPair(provider, new MemoryAuthHandler(), new NullTextureProvider());
        launchServerConfigManager.config.auth.put("std", pair);
        launchServer.reload(LaunchServer.ReloadType.FULL);
    }
}
