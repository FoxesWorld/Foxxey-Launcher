package pro.gravit.launcher.events.request;

import pro.gravit.launcher.LauncherNetworkAPI;
import pro.gravit.launcher.events.RequestEvent;
import pro.gravit.launcher.news.News;

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
