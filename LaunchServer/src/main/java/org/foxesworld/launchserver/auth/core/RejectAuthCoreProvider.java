package org.foxesworld.launchserver.auth.core;

import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.manangers.AuthManager;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;

import java.io.IOException;
import java.util.UUID;

public class RejectAuthCoreProvider extends AuthCoreProvider {
    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public User getUserByUUID(UUID uuid) {
        return null;
    }

    @Override
    public UserSession getUserSessionByOAuthAccessToken(String accessToken) {
        return null;
    }

    @Override
    public AuthManager.AuthReport refreshAccessToken(String refreshToken, AuthResponse.AuthContext context) {
        return null;
    }

    @Override
    public void verifyAuth(AuthResponse.AuthContext context) throws AuthException {
        throw new AuthException("Please configure AuthCoreProvider");
    }

    @Override
    public PasswordVerifyReport verifyPassword(User user, AuthRequest.AuthPasswordInterface password) {
        return PasswordVerifyReport.FAILED;
    }

    @Override
    public AuthManager.AuthReport createOAuthSession(User user, AuthResponse.AuthContext context, PasswordVerifyReport report, boolean minecraftAccess) {
        return null;
    }

    @Override
    public void init(LaunchServer server) {

    }

    @Override
    protected boolean updateServerID(User user, String serverID) throws IOException {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
