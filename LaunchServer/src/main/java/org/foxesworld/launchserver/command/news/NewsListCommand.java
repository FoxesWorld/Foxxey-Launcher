package org.foxesworld.launchserver.command.news;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.news.News;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

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
