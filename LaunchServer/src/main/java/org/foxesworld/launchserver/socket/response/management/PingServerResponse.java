package org.foxesworld.launchserver.socket.response.management;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.PingServerRequestEvent;
import org.foxesworld.launcher.request.management.PingServerReportRequest;
import org.foxesworld.launchserver.auth.protect.interfaces.ProfilesProtectHandler;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PingServerResponse extends SimpleResponse {
    public List<String> serverNames; //May be null

    @Override
    public String getType() {
        return "pingServer";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        Map<String, PingServerReportRequest.PingServerReport> map = new HashMap<>();
        if (serverNames == null) {
            server.pingServerManager.map.forEach((name, entity) -> {
                if (server.config.protectHandler instanceof ProfilesProtectHandler) {
                    if (!((ProfilesProtectHandler) server.config.protectHandler).canGetProfile(entity.profile, client)) {
                        return;
                    }
                }
                if (!entity.isExpired()) {
                    map.put(name, entity.lastReport);
                }
            });
        } else {
            sendError("Not implemented");
            return;
        }
        sendResult(new PingServerRequestEvent(map));
    }
}
