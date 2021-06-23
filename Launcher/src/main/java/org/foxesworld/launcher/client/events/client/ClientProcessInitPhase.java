package org.foxesworld.launcher.client.events.client;

import org.foxesworld.launcher.LauncherEngine;
import org.foxesworld.launcher.client.ClientLauncherProcess;
import org.foxesworld.launcher.modules.events.InitPhase;

public class ClientProcessInitPhase extends InitPhase {
    public final LauncherEngine clientInstance;
    public final ClientLauncherProcess.ClientParams params;

    public ClientProcessInitPhase(LauncherEngine clientInstance, ClientLauncherProcess.ClientParams params) {
        this.clientInstance = clientInstance;
        this.params = params;
    }
}
