package pro.gravit.launchserver.news;

import pro.gravit.launcher.news.News;
import pro.gravit.launchserver.LaunchServer;

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
