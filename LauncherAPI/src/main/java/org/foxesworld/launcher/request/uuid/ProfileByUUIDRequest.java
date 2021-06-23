package org.foxesworld.launcher.request.uuid;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.ProfileByUUIDRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;

import java.util.Objects;
import java.util.UUID;

public final class ProfileByUUIDRequest extends Request<ProfileByUUIDRequestEvent> implements WebSocketRequest {
    @LauncherNetworkAPI
    public final UUID uuid;


    public ProfileByUUIDRequest(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid, "uuid");
    }

    @Override
    public String getType() {
        return "profileByUUID";
    }
}
