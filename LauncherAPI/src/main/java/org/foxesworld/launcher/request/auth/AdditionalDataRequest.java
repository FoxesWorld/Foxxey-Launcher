package org.foxesworld.launcher.request.auth;

import org.foxesworld.launcher.events.request.AdditionalDataRequestEvent;
import org.foxesworld.launcher.request.Request;

import java.util.UUID;

public class AdditionalDataRequest extends Request<AdditionalDataRequestEvent> {
    public String username;
    public UUID uuid;

    public AdditionalDataRequest(String username) {
        this.username = username;
    }

    public AdditionalDataRequest(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getType() {
        return "additionalData";
    }
}