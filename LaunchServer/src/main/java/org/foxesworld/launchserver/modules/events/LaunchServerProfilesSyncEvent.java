package org.foxesworld.launchserver.modules.events;

import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launchserver.LaunchServer;

public class LaunchServerProfilesSyncEvent extends LauncherModule.Event {
    public final LaunchServer server;

    public LaunchServerProfilesSyncEvent(LaunchServer server) {
        this.server = server;
    }
}
