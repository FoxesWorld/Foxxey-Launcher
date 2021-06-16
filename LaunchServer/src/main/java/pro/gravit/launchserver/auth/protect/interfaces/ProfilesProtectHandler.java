package pro.gravit.launchserver.auth.protect.interfaces;

import pro.gravit.launcher.profiles.ClientProfile;
import pro.gravit.launchserver.socket.Client;

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
