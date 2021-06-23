package org.foxesworld.launchserver.auth.protect.interfaces;

import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launchserver.socket.Client;

public interface ProfilesProtectHandler {
    default boolean canGetProfiles(@SuppressWarnings("unused") Client client) {
        return true;
    }

    default boolean canGetProfile(ClientProfile profile, Client client) {
        return true;
    }

    default boolean canChangeProfile(ClientProfile profile, Client client) {
        return client.isAuth;
    }

    default boolean canGetUpdates(String updatesDirName, Client client) {
        return true;
    }
}
