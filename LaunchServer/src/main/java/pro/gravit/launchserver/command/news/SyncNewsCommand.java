package pro.gravit.launchserver.command.news;

import pro.gravit.launchserver.LaunchServer;
import pro.gravit.launchserver.command.Command;

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
