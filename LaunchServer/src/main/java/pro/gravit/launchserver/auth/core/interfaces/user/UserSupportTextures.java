package pro.gravit.launchserver.auth.core.interfaces.user;

import pro.gravit.launcher.profiles.ClientProfile;
import pro.gravit.launcher.profiles.Texture;

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
