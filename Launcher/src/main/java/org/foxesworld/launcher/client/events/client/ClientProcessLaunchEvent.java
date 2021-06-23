package org.foxesworld.launcher.client.events.client;

import org.foxesworld.launcher.LauncherEngine;
import org.foxesworld.launcher.client.ClientLauncherProcess;
import org.foxesworld.launcher.modules.LauncherModule;

public class ClientProcessLaunchEvent extends LauncherModule.Event {
    public final LauncherEngine clientInstance;
    public final ClientLauncherProcess.ClientParams params;

    public ClientProcessLaunchEvent(LauncherEngine clientInstance, ClientLauncherProcess.ClientParams params) {
        this.clientInstance = clientInstance;
        this.params = params;
    }
}
