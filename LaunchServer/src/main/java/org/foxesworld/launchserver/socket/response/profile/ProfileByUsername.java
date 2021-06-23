package org.foxesworld.launchserver.socket.response.profile;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.ProfileByUsernameRequestEvent;
import org.foxesworld.launcher.profiles.PlayerProfile;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

public class ProfileByUsername extends SimpleResponse {
    String username;
    @SuppressWarnings("unused")
    String client;

    @Override
    public String getType() {
        return "profileByUsername";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) throws Exception {
        AuthProviderPair pair = client.auth;
        if (pair == null) pair = server.config.getAuthProviderPair();
        PlayerProfile profile = server.authManager.getPlayerProfile(pair, username);
        if (profile == null) {
            sendError("User not found");
            return;
        }
        sendResult(new ProfileByUsernameRequestEvent(profile));
    }
}
