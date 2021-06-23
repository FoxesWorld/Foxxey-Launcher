package org.foxesworld.launchserver.command.basic;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

public final class ReloadCommand extends Command {

    public ReloadCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Reload LaunchServer";
    }

    @Override
    public void invoke(String... args) throws Exception {
        server.reload(LaunchServer.ReloadType.FULL);
    }
}
