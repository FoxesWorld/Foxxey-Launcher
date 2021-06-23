package org.foxesworld.launcher.request.management;

import org.foxesworld.launcher.events.request.FeaturesRequestEvent;
import org.foxesworld.launcher.request.Request;

public class FeaturesRequest extends Request<FeaturesRequestEvent> {
    @Override
    public String getType() {
        return "features";
    }
}
