package pro.gravit.launchserver.command.news;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pro.gravit.launcher.news.News;
import pro.gravit.launchserver.LaunchServer;
import pro.gravit.launchserver.command.Command;

import java.util.List;

public final class NewsListCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();
    public NewsListCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Show news";
    }

    @Override
    public void invoke(String... args) throws Exception {
        List<News> newsList = server.getNews();
        logger.info("Summary news: " + newsList.size());
        logger.info("- - - - - - - - - - - / News / - - - - - - - - - - -");
        if (newsList.size() == 0) {
            logger.info("Here is nothing");
        } else {
            newsList.forEach(logger::info);
        }
        logger.info("- - - - - - - - - - - / News / - - - - - - - - - - -");
    }
}
