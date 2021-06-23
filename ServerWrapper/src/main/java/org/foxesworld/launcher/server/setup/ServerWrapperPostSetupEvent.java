package org.foxesworld.launcher.server.setup;

import org.foxesworld.launcher.modules.LauncherModule;

public class ServerWrapperPostSetupEvent extends LauncherModule.Event {
    public final ServerWrapperSetup setup;

    public ServerWrapperPostSetupEvent(ServerWrapperSetup setup) {
        this.setup = setup;
    }
}
