package org.foxesworld.launcher.request.auth;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.RestoreSessionRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;

import java.util.UUID;

public class RestoreSessionRequest extends Request<RestoreSessionRequestEvent> implements WebSocketRequest {
    @LauncherNetworkAPI
    public final UUID session;
    public boolean needUserInfo;

    public RestoreSessionRequest(UUID session) {
        this.session = session;
    }

    public RestoreSessionRequest(UUID session, boolean needUserInfo) {
        this.session = session;
        this.needUserInfo = needUserInfo;
    }

    @Override
    public String getType() {
        return "restoreSession";
    }
}
