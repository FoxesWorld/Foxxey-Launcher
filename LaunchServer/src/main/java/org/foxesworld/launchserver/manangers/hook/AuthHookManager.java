package org.foxesworld.launchserver.manangers.hook;

import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;
import org.foxesworld.launchserver.socket.response.auth.CheckServerResponse;
import org.foxesworld.launchserver.socket.response.auth.JoinServerResponse;
import org.foxesworld.launchserver.socket.response.auth.SetProfileResponse;
import org.foxesworld.utils.BiHookSet;
import org.foxesworld.utils.HookSet;

public class AuthHookManager {
    public final BiHookSet<AuthResponse.AuthContext, Client> preHook = new BiHookSet<>();
    public final BiHookSet<AuthResponse.AuthContext, Client> postHook = new BiHookSet<>();
    public final BiHookSet<CheckServerResponse, Client> checkServerHook = new BiHookSet<>();
    public final BiHookSet<JoinServerResponse, Client> joinServerHook = new BiHookSet<>();
    public final BiHookSet<SetProfileResponse, Client> setProfileHook = new BiHookSet<>();
    public final HookSet<RegContext> registraion = new HookSet<>();

    public static class RegContext {
        public final String login;
        public final String password;
        public final String ip;
        public final boolean trustContext;

        public RegContext(String login, String password, String ip, boolean trustContext) {
            this.login = login;
            this.password = password;
            this.ip = ip;
            this.trustContext = trustContext;
        }
    }
}
