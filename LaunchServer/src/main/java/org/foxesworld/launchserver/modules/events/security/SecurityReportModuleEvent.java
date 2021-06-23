package org.foxesworld.launchserver.modules.events.security;

import org.foxesworld.launcher.events.request.SecurityReportRequestEvent;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.secure.SecurityReportResponse;

public class SecurityReportModuleEvent extends LauncherModule.Event {
    public final SecurityReportRequestEvent event;
    public final SecurityReportResponse response;
    public final Client client;

    public SecurityReportModuleEvent(SecurityReportRequestEvent event, SecurityReportResponse response, Client client) {
        this.event = event;
        this.response = response;
        this.client = client;
    }
}
