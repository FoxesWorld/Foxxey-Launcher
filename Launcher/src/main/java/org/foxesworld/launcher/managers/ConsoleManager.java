package org.foxesworld.launcher.managers;

import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.LauncherEngine;
import org.foxesworld.launcher.client.events.ClientUnlockConsoleEvent;
import org.foxesworld.launcher.console.UnlockCommand;
import org.foxesworld.launcher.console.test.PrintHardwareInfoCommand;
import org.foxesworld.utils.command.CommandHandler;
import org.foxesworld.utils.command.JLineCommandHandler;
import org.foxesworld.utils.command.StdCommandHandler;
import org.foxesworld.utils.command.basic.ClearCommand;
import org.foxesworld.utils.command.basic.DebugCommand;
import org.foxesworld.utils.command.basic.GCCommand;
import org.foxesworld.utils.command.basic.HelpCommand;
import org.foxesworld.utils.helper.CommonHelper;
import org.foxesworld.utils.helper.LogHelper;

import java.io.IOException;

public class ConsoleManager {
    public static CommandHandler handler;
    public static Thread thread;
    public static boolean isConsoleUnlock = false;

    public static void initConsole() throws IOException {
        CommandHandler localCommandHandler;
        try {
            Class.forName("org.jline.terminal.Terminal");

            // JLine2 available
            localCommandHandler = new JLineCommandHandler();
            LogHelper.info("JLine2 terminal enabled");
        } catch (ClassNotFoundException ignored) {
            localCommandHandler = new StdCommandHandler(true);
            LogHelper.warning("JLine2 isn't in classpath, using std");
        }
        handler = localCommandHandler;
        registerCommands();
        thread = CommonHelper.newThread("Launcher Console", true, handler);
        thread.start();
    }

    public static void registerCommands() {
        handler.registerCommand("help", new HelpCommand(handler));
        handler.registerCommand("gc", new GCCommand());
        handler.registerCommand("clear", new ClearCommand(handler));
        handler.registerCommand("unlock", new UnlockCommand());
        handler.registerCommand("printhardware", new PrintHardwareInfoCommand());
    }

    public static boolean checkUnlockKey(String key) {
        return key.equals(Launcher.getConfig().oemUnlockKey);
    }

    public static boolean unlock() {
        if (isConsoleUnlock) return true;
        ClientUnlockConsoleEvent event = new ClientUnlockConsoleEvent(handler);
        LauncherEngine.modulesManager.invokeEvent(event);
        if (event.isCancel()) return false;
        handler.registerCommand("debug", new DebugCommand());
        handler.unregisterCommand("unlock");
        isConsoleUnlock = true;
        return true;
    }
}
