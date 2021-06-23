package org.foxesworld.launcher.client.events;

import org.foxesworld.launcher.gui.RuntimeProvider;
import org.foxesworld.launcher.modules.LauncherModule;

public class ClientGuiPhase extends LauncherModule.Event {
    public final RuntimeProvider runtimeProvider;

    public ClientGuiPhase(RuntimeProvider runtimeProvider) {
        this.runtimeProvider = runtimeProvider;
    }
}
