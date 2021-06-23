package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.events.RequestEvent;
import org.foxesworld.launcher.request.management.PingServerReportRequest;

import java.util.Map;

public class PingServerRequestEvent extends RequestEvent {
    public Map<String, PingServerReportRequest.PingServerReport> serverMap;

    public PingServerRequestEvent() {
    }

    public PingServerRequestEvent(Map<String, PingServerReportRequest.PingServerReport> serverMap) {
        this.serverMap = serverMap;
    }

    @Override
    public String getType() {
        return "pingServer";
    }
}
