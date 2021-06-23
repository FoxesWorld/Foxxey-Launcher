package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.events.RequestEvent;

public class RefreshTokenRequestEvent extends RequestEvent {
    public AuthRequestEvent.OAuthRequestEvent oauth;

    public RefreshTokenRequestEvent(AuthRequestEvent.OAuthRequestEvent oauth) {
        this.oauth = oauth;
    }

    @Override
    public String getType() {
        return "refreshToken";
    }
}
