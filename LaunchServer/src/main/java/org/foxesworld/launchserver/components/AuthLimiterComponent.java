package org.foxesworld.launchserver.components;

import org.foxesworld.launcher.NeedGarbageCollection;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;
import org.foxesworld.utils.HookException;

public class AuthLimiterComponent extends IPLimiter implements NeedGarbageCollection, AutoCloseable {
    public String message;
    private transient LaunchServer srv;

    @Override
    public void init(LaunchServer launchServer) {
        srv = launchServer;
        launchServer.authHookManager.preHook.registerHook(this::preAuthHook);
    }

    public boolean preAuthHook(AuthResponse.AuthContext context, Client client) {
        if (!check(context.ip)) {
            throw new HookException(message);
        }
        return false;
    }

    @Override
    public void close() {
        srv.authHookManager.preHook.unregisterHook(this::preAuthHook);
    }
}
