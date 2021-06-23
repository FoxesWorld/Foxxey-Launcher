package org.foxesworld.launchserver.auth.provider;

import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launchserver.dao.User;

public class AuthProviderDAOResult extends AuthProviderResult {
    public User daoObject;

    @SuppressWarnings("unused")
    public AuthProviderDAOResult(String username, String accessToken) {
        super(username, accessToken);
    }

    @SuppressWarnings("unused")
    public AuthProviderDAOResult(String username, String accessToken, ClientPermissions permissions) {
        super(username, accessToken, permissions, 0, 4);
    }

    public AuthProviderDAOResult(String username, String accessToken, ClientPermissions permissions, User daoObject) {
        super(username, accessToken, permissions, 0, 4);
        this.daoObject = daoObject;
    }
}
