package org.foxesworld.launchserver.auth.protect;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;
import org.foxesworld.utils.ProviderMap;

public abstract class ProtectHandler {
    public static final ProviderMap<ProtectHandler> providers = new ProviderMap<>("ProtectHandler");
    private static boolean registredHandl = false;


    public static void registerHandlers() {
        if (!registredHandl) {
            providers.register("none", NoProtectHandler.class);
            providers.register("std", StdProtectHandler.class);
            providers.register("advanced", AdvancedProtectHandler.class);
            registredHandl = true;
        }
    }

    public abstract boolean allowGetAccessToken(AuthResponse.AuthContext context);

    public abstract void checkLaunchServerLicense(); //Выдает SecurityException при ошибке проверки лицензии

    public void init(LaunchServer server) {

    }

    public void close() {

    }
    //public abstract
}
