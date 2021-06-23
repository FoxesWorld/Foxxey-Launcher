package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.RequestEvent;
import org.foxesworld.launcher.news.News;

import java.util.List;

public final class GetNewsEvent extends RequestEvent {

    @LauncherNetworkAPI
    public List<News> news;

    public GetNewsEvent() {
    }

    public GetNewsEvent(List<News> news) {
        this.news = news;
    }

    @Override
    public String getType() {
        return "news";
    }
}
