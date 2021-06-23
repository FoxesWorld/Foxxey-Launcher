package org.foxesworld.launcher.server.setup;

import org.foxesworld.launcher.modules.LauncherModule;

public class ServerWrapperPreSetupEvent extends LauncherModule.Event {
    public final ServerWrapperSetup setup;

    public ServerWrapperPreSetupEvent(ServerWrapperSetup setup) {
        this.setup = setup;
    }
}
