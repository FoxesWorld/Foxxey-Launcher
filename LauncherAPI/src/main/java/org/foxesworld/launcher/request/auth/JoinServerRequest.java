package org.foxesworld.launcher.request.auth;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.JoinServerRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;
import org.foxesworld.utils.helper.VerifyHelper;

public final class JoinServerRequest extends Request<JoinServerRequestEvent> implements WebSocketRequest {

    // Instance
    @LauncherNetworkAPI
    public final String username;
    @LauncherNetworkAPI
    public final String accessToken;
    @LauncherNetworkAPI
    public final String serverID;


    public JoinServerRequest(String username, String accessToken, String serverID) {
        this.username = VerifyHelper.verifyUsername(username);
        this.accessToken = accessToken;
        this.serverID = VerifyHelper.verifyServerID(serverID);
    }

    @Override
    public String getType() {
        return "joinServer";
    }
}
