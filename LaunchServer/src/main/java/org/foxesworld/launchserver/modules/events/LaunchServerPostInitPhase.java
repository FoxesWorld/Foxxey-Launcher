package org.foxesworld.launchserver.modules.events;

import org.foxesworld.launcher.modules.events.PostInitPhase;
import org.foxesworld.launchserver.LaunchServer;

public class LaunchServerPostInitPhase extends PostInitPhase {
    public final LaunchServer server;

    public LaunchServerPostInitPhase(LaunchServer server) {
        this.server = server;
    }
}
