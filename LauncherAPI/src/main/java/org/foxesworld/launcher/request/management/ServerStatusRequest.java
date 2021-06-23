package org.foxesworld.launcher.request.management;

import org.foxesworld.launcher.events.request.ServerStatusRequestEvent;
import org.foxesworld.launcher.request.Request;

public class ServerStatusRequest extends Request<ServerStatusRequestEvent> {
    @Override
    public String getType() {
        return "serverStatus";
    }
}
