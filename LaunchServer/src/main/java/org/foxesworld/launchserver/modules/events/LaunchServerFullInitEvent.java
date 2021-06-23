package org.foxesworld.launchserver.modules.events;

import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launchserver.LaunchServer;

public class LaunchServerFullInitEvent extends LauncherModule.Event {
    public final LaunchServer server;

    public LaunchServerFullInitEvent(LaunchServer server) {
        this.server = server;
    }
}
