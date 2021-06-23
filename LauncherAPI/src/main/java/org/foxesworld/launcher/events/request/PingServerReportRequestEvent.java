package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.events.RequestEvent;

public class PingServerReportRequestEvent extends RequestEvent {
    @Override
    public String getType() {
        return "pingServerReport";
    }
}
