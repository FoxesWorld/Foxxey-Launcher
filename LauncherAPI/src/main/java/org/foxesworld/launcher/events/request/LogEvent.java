package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.request.WebSocketEvent;

public class LogEvent implements WebSocketEvent {
    @LauncherNetworkAPI
    public final String string;

    public LogEvent(String string) {
        this.string = string;
    }

    @Override
    public String getType() {
        return "log";
    }
}
