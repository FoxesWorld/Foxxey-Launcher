package org.foxesworld.launcher.profiles.optional.triggers;

import org.foxesworld.launcher.profiles.optional.OptionalFile;
import org.foxesworld.utils.ProviderMap;

public abstract class OptionalTrigger {
    public static ProviderMap<OptionalTrigger> providers = new ProviderMap<>("OptionalTriggers");
    private static boolean isRegisteredProviders = false;

    public static void registerProviders() {
        if (!isRegisteredProviders) {
            providers.register("java", JavaTrigger.class);
            providers.register("os", OSTrigger.class);
            isRegisteredProviders = true;
        }
    }

    public boolean required;
    public boolean inverted;

    protected abstract boolean isTriggered(OptionalFile optional, OptionalTriggerContext context);

    public boolean check(OptionalFile optional, OptionalTriggerContext context) {
        boolean result = isTriggered(optional, context);
        if (inverted) result = !result;
        return result;
    }
}
