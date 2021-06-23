package org.foxesworld.launcher.server.setup;

import org.foxesworld.launcher.modules.LauncherModule;

public class ServerWrapperSetupEvent extends LauncherModule.Event {
    public final ServerWrapperSetup setup;

    public ServerWrapperSetupEvent(ServerWrapperSetup setup) {
        this.setup = setup;
    }
}
