package org.foxesworld.launchserver.command.hash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

import java.io.IOException;

public final class SyncBinariesCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public SyncBinariesCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Resync launcher binaries";
    }

    @Override
    public void invoke(String... args) throws IOException {
        server.syncLauncherBinaries();
        logger.info("Binaries successfully resynced");
    }
}
