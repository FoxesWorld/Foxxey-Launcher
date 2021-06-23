package org.foxesworld.launchserver.news;

import org.foxesworld.launcher.news.News;
import org.foxesworld.launchserver.LaunchServer;

import java.util.Collections;
import java.util.List;

public final class EmptyNewsProvider extends NewsProvider {
    @Override
    public void init(LaunchServer launchServer) {
    }

    @Override
    public List<News> getNews() {
        return Collections.emptyList();
    }
}
