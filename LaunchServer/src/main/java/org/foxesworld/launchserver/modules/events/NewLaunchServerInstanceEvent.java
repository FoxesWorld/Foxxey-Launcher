package org.foxesworld.launchserver.modules.events;

import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launchserver.LaunchServer;

public class NewLaunchServerInstanceEvent extends LauncherModule.Event {
    public final LaunchServer launchServer;

    public NewLaunchServerInstanceEvent(LaunchServer launchServer) {
        this.launchServer = launchServer;
    }
}
