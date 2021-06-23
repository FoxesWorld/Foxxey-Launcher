package org.foxesworld.launchserver.client;

import java.util.List;

import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.utils.ProviderMap;

public abstract class ClientProfileProvider {

    public static final ProviderMap<ClientProfileProvider> providers = new ProviderMap<>("ClientProfileProvider");
    private static boolean registerProviders;

    public static void registerProviders() {
        if (!registerProviders) {
            providers.register("mysql", MysqlClientProfileProvider.class);
            registerProviders = true;
        }
    }

    public abstract List<ClientProfile> getAll();

    public abstract void init(LaunchServer launchServer);

    public abstract void close();
}
