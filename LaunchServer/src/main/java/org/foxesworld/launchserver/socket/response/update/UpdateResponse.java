package org.foxesworld.launchserver.socket.response.update;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.UpdateRequestEvent;
import org.foxesworld.launcher.hasher.HashedDir;
import org.foxesworld.launchserver.auth.protect.interfaces.ProfilesProtectHandler;
import org.foxesworld.launchserver.config.LaunchServerConfig;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;
import org.foxesworld.utils.helper.IOHelper;

public class UpdateResponse extends SimpleResponse {
    public String dirName;

    @Override
    public String getType() {
        return "update";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        if (server.config.protectHandler instanceof ProfilesProtectHandler && !((ProfilesProtectHandler) server.config.protectHandler).canGetUpdates(dirName, client)) {
            sendError("Access denied");
            return;
        }
        if (dirName == null) {
            sendError("Invalid request");
            return;
        }
        HashedDir dir = server.updatesManager.getUpdate(dirName);
        if (dir == null) {
            sendError(String.format("Directory %s not found", dirName));
            return;
        }
        String url = server.config.netty.downloadURL.replace("%dirname%", IOHelper.urlEncode(dirName));
        boolean zip = false;
        if (server.config.netty.bindings.get(dirName) != null) {
            LaunchServerConfig.NettyUpdatesBind bind = server.config.netty.bindings.get(dirName);
            url = bind.url;
            zip = bind.zip;
        }
        sendResult(new UpdateRequestEvent(dir, url, zip));
    }
}
