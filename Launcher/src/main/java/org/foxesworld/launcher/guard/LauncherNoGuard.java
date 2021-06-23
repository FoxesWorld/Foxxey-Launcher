package org.foxesworld.launcher.guard;

import org.foxesworld.launcher.client.ClientLauncherProcess;

public class LauncherNoGuard implements LauncherGuardInterface {
    @Override
    public String getName() {
        return "noGuard";
    }

    @Override
    public void applyGuardParams(ClientLauncherProcess process) {
        //IGNORED
    }
}
