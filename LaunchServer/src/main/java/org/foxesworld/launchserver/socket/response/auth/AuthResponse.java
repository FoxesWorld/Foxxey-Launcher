package org.foxesworld.launchserver.socket.response.auth;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.AuthRequestEvent;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.manangers.AuthManager;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;
import org.foxesworld.utils.HookException;

import java.util.UUID;

public class AuthResponse extends SimpleResponse {
    public String login;
    public String client;
    public boolean getSession;
    public String hardwareId;

    public AuthRequest.AuthPasswordInterface password;

    public String auth_id;
    public ConnectTypes authType;

    @Override
    public String getType() {
        return "auth";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client clientData) throws Exception {
        try {
            AuthRequestEvent result = new AuthRequestEvent();
            AuthProviderPair pair;
            if (auth_id == null || auth_id.isEmpty()) pair = server.config.getAuthProviderPair();
            else pair = server.config.getAuthProviderPair(auth_id);
            if (pair == null) {
                sendError("auth_id incorrect");
                return;
            }
            AuthContext context = server.authManager.makeAuthContext(clientData, authType, pair, login, client, ip, hardwareId);
            server.authManager.check(context);
            password = server.authManager.decryptPassword(password);
            server.authHookManager.preHook.hook(context, clientData);
            context.report = server.authManager.auth(context, password);
            server.authHookManager.postHook.hook(context, clientData);
            if (context.report.isUsingOAuth()) {
                result.oauth = new AuthRequestEvent.OAuthRequestEvent(context.report.oauthAccessToken, context.report.oauthRefreshToken, context.report.oauthExpire);
            } else if (getSession) {
                if (clientData.session == null) {
                    clientData.session = UUID.randomUUID();
                    //server.sessionManager.addClient(clientData);
                }
                result.session = clientData.session;
            }
            if (context.report.minecraftAccessToken != null) {
                result.accessToken = context.report.minecraftAccessToken;
            }
            result.playerProfile = server.authManager.getPlayerProfile(clientData);
            result.balance = context.client.balance;
            result.groupId = context.client.groupId;
            sendResult(result);
        } catch (AuthException | HookException e) {
            sendError(e.getMessage());
        }
    }

    public enum ConnectTypes {
        @Deprecated
        SERVER,
        CLIENT,
        API
    }

    public static class AuthContext {
        public final String login;
        public final String profileName;
        public final String ip;
        public final String hwid;
        public final ConnectTypes authType;
        public final Client client;
        public final AuthProviderPair pair;
        public AuthManager.AuthReport report;
        @Deprecated
        public int password_length; //Use AuthProvider for get password

        public AuthContext(Client client, String login, String profileName, String ip, ConnectTypes authType, AuthProviderPair pair, String hwid) {
            this.client = client;
            this.login = login;
            this.profileName = profileName;
            this.ip = ip;
            this.authType = authType;
            this.pair = pair;
            this.hwid = hwid;
        }
    }
}
