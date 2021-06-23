package org.foxesworld.launcher.client.events;

import org.foxesworld.launcher.LauncherEngine;
import org.foxesworld.launcher.modules.events.InitPhase;

public class ClientEngineInitPhase extends InitPhase {
    public final LauncherEngine engine;

    public ClientEngineInitPhase(LauncherEngine engine) {
        this.engine = engine;
    }
}
