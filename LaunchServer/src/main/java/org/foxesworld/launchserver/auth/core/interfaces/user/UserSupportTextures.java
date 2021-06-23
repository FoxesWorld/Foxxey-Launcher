package org.foxesworld.launchserver.auth.core.interfaces.user;

import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launcher.profiles.Texture;

public interface UserSupportTextures {
    Texture getSkinTexture();

    Texture getCloakTexture();

    @SuppressWarnings("unused")
    default Texture getSkinTexture(ClientProfile profile) {
        return getSkinTexture();
    }

    @SuppressWarnings("unused")
    default Texture getCloakTexture(ClientProfile profile) {
        return getCloakTexture();
    }
}
