package org.foxesworld.launcher.client.events;

import org.foxesworld.launcher.gui.RuntimeProvider;
import org.foxesworld.launcher.modules.LauncherModule;

public class ClientPreGuiPhase extends LauncherModule.Event {
    public RuntimeProvider runtimeProvider;

    public ClientPreGuiPhase(RuntimeProvider runtimeProvider) {
        this.runtimeProvider = runtimeProvider;
    }
}
