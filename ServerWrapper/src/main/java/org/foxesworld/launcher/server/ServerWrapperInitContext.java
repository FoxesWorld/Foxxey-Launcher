package org.foxesworld.launcher.server;

import org.foxesworld.launcher.modules.LauncherInitContext;

public class ServerWrapperInitContext implements LauncherInitContext {
    public final ServerWrapper serverWrapper;

    public ServerWrapperInitContext(ServerWrapper serverWrapper) {
        this.serverWrapper = serverWrapper;
    }
}
