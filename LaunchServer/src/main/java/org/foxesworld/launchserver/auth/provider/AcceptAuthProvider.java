package org.foxesworld.launchserver.auth.provider;

import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.utils.helper.SecurityHelper;

public final class AcceptAuthProvider extends AuthProvider {

    @Override
    public AuthProviderResult auth(String login, AuthRequest.AuthPasswordInterface password, String ip, String hwid) {
        return new AuthProviderResult(login, SecurityHelper.randomStringToken(), ClientPermissions.DEFAULT, 0, 4); // Same as login
    }

    @Override
    public void close() {
        // Do nothing
    }
}
