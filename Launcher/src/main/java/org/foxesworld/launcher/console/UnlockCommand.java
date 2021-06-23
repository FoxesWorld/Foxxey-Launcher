package org.foxesworld.launcher.console;

import org.foxesworld.launcher.managers.ConsoleManager;
import org.foxesworld.launcher.managers.SettingsManager;
import org.foxesworld.utils.command.Command;
import org.foxesworld.utils.helper.LogHelper;

public class UnlockCommand extends Command {
    @Override
    public String getArgsDescription() {
        return "[key]";
    }

    @Override
    public String getUsageDescription() {
        return "Unlock console commands";
    }

    @Override
    public void invoke(String... args) throws Exception {
        verifyArgs(args, 1);
        if (ConsoleManager.checkUnlockKey(args[0])) {
            LogHelper.info("Unlock successful");
            if (!ConsoleManager.unlock()) {
                LogHelper.error("Console unlock canceled");
                return;
            }
            LogHelper.info("Write unlock key");
            SettingsManager.settings.consoleUnlockKey = args[0];
        } else {
            LogHelper.error("Unlock key incorrect");
        }
    }
}
