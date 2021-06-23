package org.foxesworld.launcher.request.uuid;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.ProfileByUsernameRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;
import org.foxesworld.utils.helper.VerifyHelper;

public final class ProfileByUsernameRequest extends Request<ProfileByUsernameRequestEvent> implements WebSocketRequest {
    @LauncherNetworkAPI
    public final String username;


    public ProfileByUsernameRequest(String username) {
        this.username = VerifyHelper.verifyUsername(username);
    }

    @Override
    public String getType() {
        return "profileByUsername";
    }
}
