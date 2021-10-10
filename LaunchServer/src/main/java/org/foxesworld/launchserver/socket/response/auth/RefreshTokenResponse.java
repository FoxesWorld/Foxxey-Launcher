package org.foxesworld.launchserver.socket.response.auth;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.AuthRequestEvent;
import org.foxesworld.launcher.events.request.RefreshTokenRequestEvent;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.manangers.AuthManager;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

public class RefreshTokenResponse extends SimpleResponse {
    public String authId;
    public String refreshToken;

    @Override
    public String getType() {
        return "refreshToken";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) throws Exception {
        if (refreshToken == null) {
            sendError("Invalid request");
            return;
        }
        AuthProviderPair pair;
        if (!client.isAuth) {
            if (authId == null) {
                pair = server.config.getAuthProviderPair();
            } else {
                pair = server.config.getAuthProviderPair(authId);
            }
        } else {
            pair = client.auth;
        }
        if (pair == null) {
            sendError("Invalid request");
            return;
        }
        AuthManager.AuthReport report = pair.core.refreshAccessToken(refreshToken, new AuthResponse.AuthContext(client, null, null, ip, AuthResponse.ConnectTypes.API, pair, null));
        if (report == null || !report.isUsingOAuth()) {
            sendError("Invalid RefreshToken");
            return;
        }
        sendResult(new RefreshTokenRequestEvent(new AuthRequestEvent.OAuthRequestEvent(report.oauthAccessToken(), report.oauthRefreshToken(), report.oauthExpire())));
    }
}
