package org.foxesworld.launchserver.socket.response;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;
import org.foxesworld.launchserver.socket.Client;

public interface WebSocketServerResponse extends WebSocketRequest {
    String getType();

    void execute(ChannelHandlerContext ctx, Client client) throws Exception;
}
