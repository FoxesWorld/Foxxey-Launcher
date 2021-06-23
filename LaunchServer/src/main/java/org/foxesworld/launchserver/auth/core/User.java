package org.foxesworld.launchserver.auth.core;

import org.foxesworld.launcher.ClientPermissions;

import java.util.UUID;

public interface User {
    String getUsername();

    UUID getUUID();

    String getServerId();

    String getAccessToken();

    ClientPermissions getPermissions();

    @SuppressWarnings("unused")
    default boolean isBanned() {
        return false;
    }
}
