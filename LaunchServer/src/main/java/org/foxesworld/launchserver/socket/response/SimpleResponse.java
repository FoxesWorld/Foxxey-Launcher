package org.foxesworld.launchserver.socket.response;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.RequestEvent;
import org.foxesworld.launcher.events.request.ErrorRequestEvent;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.socket.WebSocketService;

import java.util.UUID;

public abstract class SimpleResponse implements WebSocketServerResponse {
    public UUID requestUUID;
    public transient LaunchServer server;
    public transient WebSocketService service;
    public transient ChannelHandlerContext ctx;
    public transient String ip;

    public void sendResult(RequestEvent result) {
        result.requestUUID = requestUUID;
        service.sendObject(ctx, result);
    }

    public void sendResultAndClose(RequestEvent result) {
        result.requestUUID = requestUUID;
        service.sendObjectAndClose(ctx, result);
    }

    public void sendError(String errorMessage) {
        ErrorRequestEvent event = new ErrorRequestEvent(errorMessage);
        event.requestUUID = requestUUID;
        service.sendObject(ctx, event);
    }
}
