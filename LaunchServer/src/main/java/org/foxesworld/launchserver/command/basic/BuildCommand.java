package org.foxesworld.launchserver.command.basic;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

public final class BuildCommand extends Command {
    public BuildCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Build launcher binaries";
    }

    @Override
    public void invoke(String... args) throws Exception {
        server.buildLauncherBinaries();
        server.syncLauncherBinaries();
    }
}
