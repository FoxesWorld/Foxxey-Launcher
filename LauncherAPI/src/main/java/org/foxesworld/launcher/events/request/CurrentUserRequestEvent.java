package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launcher.events.RequestEvent;
import org.foxesworld.launcher.profiles.PlayerProfile;

public class CurrentUserRequestEvent extends RequestEvent {
    public final UserInfo userInfo;

    public CurrentUserRequestEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getType() {
        return "currentUser";
    }

    public static class UserInfo {
        public ClientPermissions permissions;
        public String accessToken;
        public PlayerProfile playerProfile;
    }
}
