package org.foxesworld.launchserver.modules.impl;

import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launchserver.LaunchServer;

public class LaunchServerInitContext implements LauncherInitContext {
    public final LaunchServer server;

    public LaunchServerInitContext(LaunchServer server) {
        this.server = server;
    }
}
