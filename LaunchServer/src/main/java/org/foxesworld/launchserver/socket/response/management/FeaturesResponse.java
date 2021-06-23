package org.foxesworld.launchserver.socket.response.management;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.FeaturesRequestEvent;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

public class FeaturesResponse extends SimpleResponse {
    @Override
    public String getType() {
        return "features";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        sendResult(new FeaturesRequestEvent(server.featuresManager.getMap()));
    }
}
