package org.foxesworld.launcher.request.auth;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.AuthRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.auth.password.*;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;
import org.foxesworld.utils.ProviderMap;

public final class AuthRequest extends Request<AuthRequestEvent> implements WebSocketRequest {
    public static final ProviderMap<AuthPasswordInterface> providers = new ProviderMap<>();
    private static boolean registerProviders = false;
    @LauncherNetworkAPI
    private final String login;
    @LauncherNetworkAPI
    private final AuthPasswordInterface password;
    @LauncherNetworkAPI
    private final String auth_id;
    @LauncherNetworkAPI
    private final boolean getSession;
    @LauncherNetworkAPI
    private final ConnectTypes authType;
    @LauncherNetworkAPI
    private final String hardwareId;

    public AuthRequest(String login, String password, String auth_id, ConnectTypes authType) {
        this.login = login;
        this.password = new AuthPlainPassword(password);
        this.auth_id = auth_id;
        this.authType = authType;
        this.getSession = false;
        this.hardwareId = null;
    }

    @Deprecated
    public AuthRequest(String login, AuthPasswordInterface password, String auth_id, boolean getSession, ConnectTypes authType) {
        this.login = login;
        this.password = password;
        this.auth_id = auth_id;
        this.getSession = getSession;
        this.authType = authType;
        this.hardwareId = null;
    }

    public AuthRequest(String login, AuthPasswordInterface password, String auth_id, boolean getSession, ConnectTypes authType, String hardwareId) {
        this.login = login;
        this.password = password;
        this.auth_id = auth_id;
        this.getSession = getSession;
        this.authType = authType;
        this.hardwareId = hardwareId;
    }

    public static void registerProviders() {
        if (!registerProviders) {
            providers.register("plain", AuthPlainPassword.class);
            providers.register("rsa2", AuthRSAPassword.class);
            providers.register("aes", AuthAESPassword.class);
            providers.register("2fa", Auth2FAPassword.class);
            providers.register("multi", AuthMultiPassword.class);
            providers.register("signature", AuthSignaturePassword.class);
            providers.register("totp", AuthTOTPPassword.class);
            providers.register("oauth", AuthOAuthPassword.class);
            providers.register("code", AuthCodePassword.class);
            registerProviders = true;
        }
    }

    @Override
    public String getType() {
        return "auth";
    }

    public enum ConnectTypes {
        @LauncherNetworkAPI
        CLIENT,
        @LauncherNetworkAPI
        API
    }

    public interface AuthPasswordInterface {
        boolean check();

        default boolean isAllowSave() {
            return false;
        }
    }
}
