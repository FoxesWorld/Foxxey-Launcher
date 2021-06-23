package org.foxesworld.launchserver.modules.events;

import org.foxesworld.launcher.modules.events.InitPhase;
import org.foxesworld.launchserver.LaunchServer;

public class LaunchServerInitPhase extends InitPhase {
    public final LaunchServer server;

    public LaunchServerInitPhase(LaunchServer server) {
        this.server = server;
    }
}
