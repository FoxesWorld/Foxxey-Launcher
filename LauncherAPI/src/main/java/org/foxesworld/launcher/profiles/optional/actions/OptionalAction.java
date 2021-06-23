package org.foxesworld.launcher.profiles.optional.actions;

import org.foxesworld.utils.ProviderMap;

public class OptionalAction {
    public static final ProviderMap<OptionalAction> providers = new ProviderMap<>();
    private static boolean registerProviders = false;

    public static void registerProviders() {
        if (!registerProviders) {
            providers.register("file", OptionalActionFile.class);
            providers.register("clientArgs", OptionalActionClientArgs.class);
            providers.register("jvmArgs", OptionalActionJvmArgs.class);
            providers.register("classpath", OptionalActionClassPath.class);
            registerProviders = true;
        }
    }
}
