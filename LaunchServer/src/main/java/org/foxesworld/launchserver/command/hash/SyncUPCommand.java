package org.foxesworld.launchserver.command.hash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

import java.io.IOException;

public final class SyncUPCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public SyncUPCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Resync profiles & updates dirs";
    }

    @Override
    public void invoke(String... args) throws IOException {
        server.syncProfiles();
        logger.info("Profiles successfully resynced");

        server.syncUpdatesDir(null);
        logger.info("Updates dir successfully resynced");
    }
}
