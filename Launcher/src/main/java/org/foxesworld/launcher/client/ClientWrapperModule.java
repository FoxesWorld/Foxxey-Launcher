package org.foxesworld.launcher.client;

import org.foxesworld.launcher.ClientLauncherWrapper;

public interface ClientWrapperModule {
    void wrapperPhase(ClientLauncherWrapper.ClientLauncherWrapperContext context);
}
