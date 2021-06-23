package org.foxesworld.launchserver.auth.protect;

import org.foxesworld.launchserver.socket.response.auth.AuthResponse;

public class NoProtectHandler extends ProtectHandler {

    @Override
    public boolean allowGetAccessToken(AuthResponse.AuthContext context) {
        return true;
    }

    @Override
    public void checkLaunchServerLicense() {
        // None
    }
}
