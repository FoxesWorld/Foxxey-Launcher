package org.foxesworld.launchserver.command.news;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

public final class SyncNewsCommand extends Command {

    public SyncNewsCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Sync news";
    }

    @Override
    public void invoke(String... args) throws Exception {
        server.config.newsProvider.sync();
    }
}
