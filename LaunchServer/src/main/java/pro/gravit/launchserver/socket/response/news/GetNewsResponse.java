package pro.gravit.launchserver.socket.response.news;

import io.netty.channel.ChannelHandlerContext;
import pro.gravit.launcher.events.request.GetNewsEvent;
import pro.gravit.launcher.news.News;
import pro.gravit.launchserver.socket.Client;
import pro.gravit.launchserver.socket.response.SimpleResponse;

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
