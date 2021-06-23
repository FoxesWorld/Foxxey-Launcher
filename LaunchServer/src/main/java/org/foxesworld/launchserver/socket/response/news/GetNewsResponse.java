package org.foxesworld.launchserver.socket.response.news;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.GetNewsEvent;
import org.foxesworld.launcher.news.News;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

import java.util.List;

public class GetNewsResponse extends SimpleResponse {
    @Override
    public String getType() {
        return "news";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) throws Exception {
        List<News> news = server.getNews();
        GetNewsEvent getNewsEvent = new GetNewsEvent(news);
        sendResult(getNewsEvent);
    }
}
