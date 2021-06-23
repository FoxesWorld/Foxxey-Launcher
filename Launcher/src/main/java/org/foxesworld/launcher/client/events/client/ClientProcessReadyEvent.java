package org.foxesworld.launcher.client.events.client;

import org.foxesworld.launcher.LauncherEngine;
import org.foxesworld.launcher.client.ClientLauncherProcess;
import org.foxesworld.launcher.modules.events.PostInitPhase;

public class ClientProcessReadyEvent extends PostInitPhase {
    public final LauncherEngine clientInstance;
    public final ClientLauncherProcess.ClientParams params;

    public ClientProcessReadyEvent(LauncherEngine clientInstance, ClientLauncherProcess.ClientParams params) {
        this.clientInstance = clientInstance;
        this.params = params;
    }
}
