package org.foxesworld.launcher.request.update;

import org.foxesworld.launcher.events.request.UpdateListRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;

public final class UpdateListRequest extends Request<UpdateListRequestEvent> implements WebSocketRequest {

    @Override
    public String getType() {
        return "updateList";
    }
}
