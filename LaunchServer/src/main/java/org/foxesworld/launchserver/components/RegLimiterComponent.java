package org.foxesworld.launchserver.components;

import org.foxesworld.launcher.NeedGarbageCollection;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.manangers.hook.AuthHookManager;
import org.foxesworld.utils.HookException;

import java.util.ArrayList;
import java.util.List;

public class RegLimiterComponent extends IPLimiter implements NeedGarbageCollection, AutoCloseable {

    public transient LaunchServer launchServer;
    public String message;

    @SuppressWarnings("unused")
    public List<String> excludeIps = new ArrayList<>();

    @Override
    public void init(LaunchServer launchServer) {
        this.launchServer = launchServer;
        launchServer.authHookManager.registraion.registerHook(this::registerHook);
    }

    public boolean registerHook(AuthHookManager.RegContext context) {
        if (!check(context.ip)) {
            throw new HookException(message);
        }
        return false;
    }

    @Override
    public void close() {
        launchServer.authHookManager.registraion.unregisterHook(this::registerHook);
    }
}
