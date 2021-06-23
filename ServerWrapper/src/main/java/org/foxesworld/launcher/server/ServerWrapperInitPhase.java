package org.foxesworld.launcher.server;

import org.foxesworld.launcher.modules.LauncherModule;

public class ServerWrapperInitPhase extends LauncherModule.Event {
    public final ServerWrapper serverWrapper;

    public ServerWrapperInitPhase(ServerWrapper serverWrapper) {
        this.serverWrapper = serverWrapper;
    }
}
