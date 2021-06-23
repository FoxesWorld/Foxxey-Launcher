package org.foxesworld.launcher.guard;

import org.foxesworld.launcher.client.ClientLauncherProcess;

public interface LauncherGuardInterface {
    String getName();

    void applyGuardParams(ClientLauncherProcess process);
}
