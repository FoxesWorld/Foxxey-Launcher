package org.foxesworld.launchserver.socket.response.auth;

import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.events.request.CheckServerRequestEvent;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.manangers.AuthManager;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;
import org.foxesworld.utils.HookException;

public class CheckServerResponse extends SimpleResponse {
    private transient final Logger logger = LogManager.getLogger();
    public String serverID;
    public String username;
    public String client;

    @Override
    public String getType() {
        return "checkServer";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client pClient) {
        if (!pClient.isAuth || pClient.type == AuthResponse.ConnectTypes.CLIENT) {
            sendError("Permissions denied");
            return;
        }
        CheckServerRequestEvent result = new CheckServerRequestEvent();
        try {
            server.authHookManager.checkServerHook.hook(this, pClient);
            AuthManager.CheckServerReport report = server.authManager.checkServer(pClient, username, serverID);
            if (report != null) {
                result.playerProfile = report.playerProfile;
                result.uuid = report.uuid;
                logger.debug("checkServer: {} uuid: {} serverID: {}", result.playerProfile == null ? null : result.playerProfile.username, result.uuid, serverID);
            }
        } catch (AuthException | HookException e) {
            sendError(e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Internal authHandler error", e);
            sendError("Internal authHandler error");
            return;
        }
        sendResult(result);
    }

}
