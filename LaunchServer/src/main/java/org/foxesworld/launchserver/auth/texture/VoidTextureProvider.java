package org.foxesworld.launchserver.auth.texture;

import org.foxesworld.launcher.profiles.Texture;

import java.util.UUID;

public final class VoidTextureProvider extends TextureProvider {

    @Override
    public void close() {
        // Do nothing
    }

    @Override
    public Texture getCloakTexture(UUID uuid, String username, String client) {
        return null; // Always nothing
    }

    @Override
    public Texture getSkinTexture(UUID uuid, String username, String client) {
        return null; // Always nothing
    }
}
