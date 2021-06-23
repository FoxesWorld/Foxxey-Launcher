package org.foxesworld.launchserver.modules.events;

import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launchserver.LaunchServer;

public class LaunchServerUpdatesSyncEvent extends LauncherModule.Event {
    public final LaunchServer server;

    public LaunchServerUpdatesSyncEvent(LaunchServer server) {
        this.server = server;
    }
}
