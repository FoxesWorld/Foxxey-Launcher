package org.foxesworld.launchserver.socket.response.secure;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.GetSecureLevelInfoRequestEvent;
import org.foxesworld.launchserver.auth.protect.interfaces.SecureProtectHandler;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

public class GetSecureLevelInfoResponse extends SimpleResponse {
    @Override
    public String getType() {
        return "getSecureLevelInfo";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        if (!(server.config.protectHandler instanceof SecureProtectHandler secureProtectHandler)) {
            GetSecureLevelInfoRequestEvent response = new GetSecureLevelInfoRequestEvent(null);
            response.enabled = false;
            sendResult(response);
            return;
        }
        if (!secureProtectHandler.allowGetSecureLevelInfo(client)) {
            sendError("Access denied");
            return;
        }
        if (client.trustLevel == null) client.trustLevel = new Client.TrustLevel();
        if (client.trustLevel.verifySecureKey == null)
            client.trustLevel.verifySecureKey = secureProtectHandler.generateSecureLevelKey();
        GetSecureLevelInfoRequestEvent response = new GetSecureLevelInfoRequestEvent(client.trustLevel.verifySecureKey);
        response.enabled = true;
        sendResult(secureProtectHandler.onGetSecureLevelInfo(response));
    }
}
