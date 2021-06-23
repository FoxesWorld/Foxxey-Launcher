package org.foxesworld.launchserver.socket.response.management;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launcher.events.request.PingServerReportRequestEvent;
import org.foxesworld.launcher.request.management.PingServerReportRequest;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

public class PingServerReportResponse extends SimpleResponse {
    public PingServerReportRequest.PingServerReport data;
    public String name;

    @Override
    public String getType() {
        return "pingServerReport";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        if (!client.isAuth || client.permissions == null || !client.permissions.isPermission(ClientPermissions.PermissionConsts.MANAGEMENT)) {
            sendError("Access denied");
            return;
        }
        server.pingServerManager.updateServer(name, data);
        sendResult(new PingServerReportRequestEvent());
    }
}
