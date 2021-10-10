package org.foxesworld.launchserver.socket.response.auth;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.CurrentUserRequestEvent;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

import java.io.IOException;

public class CurrentUserResponse extends SimpleResponse {

    public static CurrentUserRequestEvent.UserInfo collectUserInfoFromClient(LaunchServer server, Client client) throws IOException {
        CurrentUserRequestEvent.UserInfo result = new CurrentUserRequestEvent.UserInfo();
        if (client.auth != null && client.isAuth && client.username != null) {
            result.playerProfile = server.authManager.getPlayerProfile(client);
        }
        result.permissions = client.permissions;
        return result;
    }

    public static CurrentUserRequestEvent.UserInfo collectUserInfoFromClient(Client client) {
        CurrentUserRequestEvent.UserInfo result = new CurrentUserRequestEvent.UserInfo();
        result.permissions = client.permissions;
        return result;
    }

    @Override
    public String getType() {
        return "currentUser";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) throws Exception {
        sendResult(new CurrentUserRequestEvent(collectUserInfoFromClient(server, client)));
    }
}
