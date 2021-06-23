package org.foxesworld.launchserver.command.basic;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

public final class RestartCommand extends Command {
    public RestartCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Restart LaunchServer";
    }

    @Override
    public void invoke(String... args) {
        server.fullyRestart();
    }
}