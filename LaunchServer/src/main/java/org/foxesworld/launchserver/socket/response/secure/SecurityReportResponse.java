package org.foxesworld.launchserver.socket.response.secure;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.SecurityReportRequestEvent;
import org.foxesworld.launchserver.auth.protect.interfaces.SecureProtectHandler;
import org.foxesworld.launchserver.modules.events.security.SecurityReportModuleEvent;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

public class SecurityReportResponse extends SimpleResponse {
    @SuppressWarnings("unused")
    public String reportType;
    @SuppressWarnings("unused")
    public String smallData;
    @SuppressWarnings("unused")
    public String largeData;
    @SuppressWarnings("unused")
    public byte[] smallBytes;
    @SuppressWarnings("unused")
    public byte[] largeBytes;

    @Override
    public String getType() {
        return "securityReport";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        if (!(server.config.protectHandler instanceof SecureProtectHandler)) {
            sendError("Method not allowed");
        }
        SecureProtectHandler secureProtectHandler = (SecureProtectHandler) server.config.protectHandler;
        SecurityReportRequestEvent event = secureProtectHandler.onSecurityReport(this, client);
        server.modulesManager.invokeEvent(new SecurityReportModuleEvent(event, this, client));
        sendResult(event);
    }
}
