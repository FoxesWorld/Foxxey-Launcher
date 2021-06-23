package org.foxesworld.launcher.profiles.optional.triggers;

import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launcher.profiles.PlayerProfile;
import org.foxesworld.utils.helper.JavaHelper;

public interface OptionalTriggerContext {
    ClientProfile getProfile();

    String getUsername();

    JavaHelper.JavaVersion getJavaVersion();

    default ClientPermissions getPermissions() {
        return ClientPermissions.DEFAULT;
    }

    default PlayerProfile getPlayerProfile() {
        return null;
    }
}