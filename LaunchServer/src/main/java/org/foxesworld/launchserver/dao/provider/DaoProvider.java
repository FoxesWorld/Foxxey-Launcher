package org.foxesworld.launchserver.dao.provider;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.dao.UserDAO;
import org.foxesworld.utils.ProviderMap;

public abstract class DaoProvider {
    public static final ProviderMap<DaoProvider> providers = new ProviderMap<>("DaoProvider");
    public transient UserDAO userDAO;

    public static void registerProviders() {
        // None
    }

    public abstract void init(LaunchServer server);
}
