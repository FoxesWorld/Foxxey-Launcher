package org.foxesworld.launcher.server;

import org.foxesworld.launcher.modules.impl.SimpleModuleManager;

import java.nio.file.Path;

public class ServerWrapperModulesManager extends SimpleModuleManager {
    public ServerWrapperModulesManager(Path modulesDir, Path configDir) {
        super(modulesDir, configDir);
    }

    public void fullInitializeServerWrapper(ServerWrapper serverWrapper) {
        initContext = new ServerWrapperInitContext(serverWrapper);
    }
}
