package org.foxesworld.launchserver.news;

import org.foxesworld.launcher.news.News;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.utils.ProviderMap;

import java.util.List;

public abstract class NewsProvider {

    public static final ProviderMap<NewsProvider> providers = new ProviderMap<>("NewsProvider");
    private static boolean registerProviders;

    public static void registerProviders() {
        if (!registerProviders) {
            providers.register("vk", VkNewsProvider.class);
            providers.register("empty", EmptyNewsProvider.class);
            registerProviders = true;
        }
    }

    public abstract void init(LaunchServer launchServer);

    public abstract List<News> getNews();

    public void close() {
        // Do nothing as default
    }

    public void sync() {
        // Do nothing as default
    }
}
