package org.foxesworld.launchserver.command.service;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

public class ConfigCommand extends Command {
    public ConfigCommand(LaunchServer server) {
        super(server.reconfigurableManager.getCommands(), server);
    }

    @Override
    public String getArgsDescription() {
        return "[name] [action] [more args]";
    }

    @Override
    public String getUsageDescription() {
        return "call reconfigurable action";
    }

    @Override
    public void invoke(String... args) throws Exception {
        invokeSubcommands(args);
    }
}
