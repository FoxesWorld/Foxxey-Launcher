package pro.gravit.launchserver.command.news;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pro.gravit.launchserver.LaunchServer;
import pro.gravit.launchserver.command.Command;

public class SyncNewsCommand extends Command {
    private transient final Logger logger = LogManager.getLogger(SyncNewsCommand.class);

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
        logger.info("News syncing..");
    }
}
