package org.foxesworld.launcher.client.events;

import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.utils.command.CommandHandler;

public class ClientUnlockConsoleEvent extends LauncherModule.Event {
    public final CommandHandler handler;

    public ClientUnlockConsoleEvent(CommandHandler handler) {
        this.handler = handler;
    }
}
