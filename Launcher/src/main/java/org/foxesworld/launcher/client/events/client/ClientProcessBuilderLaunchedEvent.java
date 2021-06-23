package org.foxesworld.launcher.client.events.client;

import org.foxesworld.launcher.client.ClientLauncherProcess;
import org.foxesworld.launcher.modules.LauncherModule;

public class ClientProcessBuilderLaunchedEvent extends LauncherModule.Event {
    public final ClientLauncherProcess processBuilder;

    public ClientProcessBuilderLaunchedEvent(ClientLauncherProcess processBuilder) {
        this.processBuilder = processBuilder;
    }
}
