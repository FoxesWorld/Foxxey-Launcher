package pro.gravit.launchserver.socket.response.auth;

import pro.gravit.launcher.profiles.ClientProfile;
import pro.gravit.utils.ProviderMap;

import java.util.List;

public abstract class ClientProfileProvider {

    public static final ProviderMap<ClientProfileProvider> providers = new ProviderMap<>(
            "ClientProfileProvider");

    public static void registerProviders() {
        providers.register("mysql", MysqlClientProfileProvider.class);
    }

    public abstract List<ClientProfile> getAll();

    public abstract void init();

    public abstract void close();
}
