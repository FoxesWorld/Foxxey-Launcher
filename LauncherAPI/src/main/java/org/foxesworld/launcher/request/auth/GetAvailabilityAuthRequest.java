package org.foxesworld.launcher.request.auth;

import org.foxesworld.launcher.events.request.GetAvailabilityAuthRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.auth.details.AuthLoginOnlyDetails;
import org.foxesworld.launcher.request.auth.details.AuthPasswordDetails;
import org.foxesworld.launcher.request.auth.details.AuthTotpDetails;
import org.foxesworld.launcher.request.auth.details.AuthWebViewDetails;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;
import org.foxesworld.utils.ProviderMap;

public class GetAvailabilityAuthRequest extends Request<GetAvailabilityAuthRequestEvent> implements WebSocketRequest {

    public static final ProviderMap<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> providers = new ProviderMap<>();
    private static boolean registeredProviders = false;

    public static void registerProviders() {
        if (!registeredProviders) {
            providers.register("password", AuthPasswordDetails.class);
            providers.register("webview", AuthWebViewDetails.class);
            providers.register("totp", AuthTotpDetails.class);
            providers.register("loginonly", AuthLoginOnlyDetails.class);
            registeredProviders = true;
        }
    }

    @Override
    public String getType() {
        return "getAvailabilityAuth";
    }
}
