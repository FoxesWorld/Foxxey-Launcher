package org.foxesworld.launchserver.socket.response.profile;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.BatchProfileByUsernameRequestEvent;
import org.foxesworld.launcher.profiles.PlayerProfile;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

public class BatchProfileByUsername extends SimpleResponse {
    Entry[] list;

    @Override
    public String getType() {
        return "batchProfileByUsername";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) throws Exception {
        BatchProfileByUsernameRequestEvent result = new BatchProfileByUsernameRequestEvent();
        if (list == null) {
            sendError("Invalid request");
            return;
        }
        result.playerProfiles = new PlayerProfile[list.length];
        for (int i = 0; i < list.length; ++i) {
            AuthProviderPair pair = client.auth;
            if (pair == null) {
                pair = server.config.getAuthProviderPair();
            }
            result.playerProfiles[i] = server.authManager.getPlayerProfile(pair, list[i].username);
        }
        sendResult(result);
    }

    static class Entry {
        String username;
        @SuppressWarnings("unused")
        String client;
    }
}
