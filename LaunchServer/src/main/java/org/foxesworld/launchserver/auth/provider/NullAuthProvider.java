package org.foxesworld.launchserver.auth.provider;

import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.utils.helper.VerifyHelper;

import java.io.IOException;
import java.util.Objects;

public final class NullAuthProvider extends AuthProvider {
    private volatile AuthProvider provider;

    @Override
    public AuthProviderResult auth(String login, AuthRequest.AuthPasswordInterface password, String ip, String hwid) throws Exception {
        return getProvider().auth(login, password, ip, hwid);
    }

    @Override
    public void close() throws IOException {
        AuthProvider provider = this.provider;
        if (provider != null)
            provider.close();
    }

    private AuthProvider getProvider() {
        return VerifyHelper.verify(provider, Objects::nonNull, "Backend auth provider wasn't set");
    }

    @SuppressWarnings("unused")
    public void setBackend(AuthProvider provider) {
        this.provider = provider;
    }
}
