package org.foxesworld.launchserver.socket.response.auth;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.RestoreSessionRequestEvent;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.handlers.WebSocketFrameHandler;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

import java.util.UUID;

public class RestoreSessionResponse extends SimpleResponse {
    @LauncherNetworkAPI
    public UUID session;
    public boolean needUserInfo;

    @Override
    public String getType() {
        return "restoreSession";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) throws Exception {
        if (session == null) {
            sendError("Session invalid");
            return;
        }
        final Client[] rClient = {null};
        service.forEachActiveChannels((channel, handler) -> {
            Client c = handler.getClient();
            if (c != null && session.equals(c.session)) {
                rClient[0] = c;
            }
        });
        if (rClient[0] == null) {
            rClient[0] = server.sessionManager.getClient(session);
        }
        if (rClient[0] == null) {
            sendError("Session invalid");
            return;
        }
        if (rClient[0].useOAuth) {
            sendError("This session using OAuth. Session restoration not safety");
            return;
        }
        WebSocketFrameHandler frameHandler = ctx.pipeline().get(WebSocketFrameHandler.class);
        frameHandler.setClient(rClient[0]);
        if (needUserInfo) {
            sendResult(new RestoreSessionRequestEvent(CurrentUserResponse.collectUserInfoFromClient(rClient[0])));
        } else {
            sendResult(new RestoreSessionRequestEvent());
        }
    }
}
