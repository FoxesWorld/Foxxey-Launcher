package org.foxesworld.launcher.request.update;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.UpdateRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;

public final class UpdateRequest extends Request<UpdateRequestEvent> implements WebSocketRequest {

    // Instance
    @LauncherNetworkAPI
    public final String dirName;

    public UpdateRequest(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public String getType() {
        return "update";
    }
}
