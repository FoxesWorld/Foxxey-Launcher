package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.RequestEvent;
import org.foxesworld.launcher.profiles.PlayerProfile;

import java.util.UUID;


public class ProfileByUUIDRequestEvent extends RequestEvent {
    @SuppressWarnings("unused")
    private static final UUID uuid = UUID.fromString("b9014cf3-4b95-4d38-8c5f-867f190a18a0");
    @LauncherNetworkAPI
    public String error;
    @LauncherNetworkAPI
    public PlayerProfile playerProfile;

    public ProfileByUUIDRequestEvent(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    public ProfileByUUIDRequestEvent() {
    }

    @Override
    public String getType() {
        return "profileByUUID";
    }
}
