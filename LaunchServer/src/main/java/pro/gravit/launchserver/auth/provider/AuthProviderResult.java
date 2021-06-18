package pro.gravit.launchserver.auth.provider;

import pro.gravit.launcher.ClientPermissions;


public class AuthProviderResult {
    public final String username;
    public final String accessToken;
    public final ClientPermissions permissions;
    public final int balance;
    public final int groupId;

    public AuthProviderResult(String username, String accessToken) {
        this.username = username;
        this.accessToken = accessToken;
        permissions = ClientPermissions.DEFAULT;
        this.balance = 0;
        this.groupId = 0;
    }

    public AuthProviderResult(String username, String accessToken, ClientPermissions permissions, int balance, int groupId) {
        this.username = username;
        this.accessToken = accessToken;
        this.permissions = permissions;
        this.balance = balance;
        this.groupId = groupId;
    }
}
