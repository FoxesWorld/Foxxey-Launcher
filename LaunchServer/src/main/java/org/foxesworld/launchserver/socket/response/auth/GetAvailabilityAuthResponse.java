package org.foxesworld.launchserver.socket.response.auth;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.GetAvailabilityAuthRequestEvent;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

import java.util.ArrayList;
import java.util.List;

public class GetAvailabilityAuthResponse extends SimpleResponse {
    @Override
    public String getType() {
        return "getAvailabilityAuth";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        List<GetAvailabilityAuthRequestEvent.AuthAvailability> list = new ArrayList<>();
        for (AuthProviderPair pair : server.config.auth.values()) {
            list.add(new GetAvailabilityAuthRequestEvent.AuthAvailability(pair.name, pair.displayName,
                    pair.isUseSocial() ? pair.social.getDetails(client) : pair.core.getDetails(client)));
        }
        sendResult(new GetAvailabilityAuthRequestEvent(list));
    }
}
