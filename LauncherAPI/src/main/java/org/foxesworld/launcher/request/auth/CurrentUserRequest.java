package org.foxesworld.launcher.request.auth;

import org.foxesworld.launcher.events.request.CurrentUserRequestEvent;
import org.foxesworld.launcher.request.Request;

public class CurrentUserRequest extends Request<CurrentUserRequestEvent> {
    @Override
    public String getType() {
        return "currentUser";
    }
}
