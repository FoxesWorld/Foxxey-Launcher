package pro.gravit.launchserver.auth.provider;

import pro.gravit.launcher.ClientPermissions;
import pro.gravit.launcher.request.auth.AuthRequest;
import pro.gravit.utils.helper.SecurityHelper;

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
