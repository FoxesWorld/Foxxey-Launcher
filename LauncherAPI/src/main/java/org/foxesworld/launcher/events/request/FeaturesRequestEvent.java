package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.events.RequestEvent;

import java.util.Map;

public class FeaturesRequestEvent extends RequestEvent {
    public Map<String, String> features;

    public FeaturesRequestEvent() {
    }

    public FeaturesRequestEvent(Map<String, String> features) {
        this.features = features;
    }

    @Override
    public String getType() {
        return "features";
    }
}
