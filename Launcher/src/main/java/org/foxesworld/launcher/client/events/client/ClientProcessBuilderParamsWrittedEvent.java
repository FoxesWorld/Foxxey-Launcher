package org.foxesworld.launcher.client.events.client;

import org.foxesworld.launcher.client.ClientLauncherProcess;
import org.foxesworld.launcher.modules.LauncherModule;

public class ClientProcessBuilderParamsWrittedEvent extends LauncherModule.Event {
    public final ClientLauncherProcess process;

    public ClientProcessBuilderParamsWrittedEvent(ClientLauncherProcess process) {
        this.process = process;
    }
}
