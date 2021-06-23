package org.foxesworld.launcher.request.secure;

import org.foxesworld.launcher.events.request.GetSecureLevelInfoRequestEvent;
import org.foxesworld.launcher.request.Request;

public class GetSecureLevelInfoRequest extends Request<GetSecureLevelInfoRequestEvent> {
    @Override
    public String getType() {
        return "getSecureLevelInfo";
    }
}
