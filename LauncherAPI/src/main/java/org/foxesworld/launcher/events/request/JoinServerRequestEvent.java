package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.RequestEvent;

import java.util.UUID;


public class JoinServerRequestEvent extends RequestEvent {
    @SuppressWarnings("unused")
    private static final UUID uuid = UUID.fromString("2a12e7b5-3f4a-4891-a2f9-ea141c8e1995");
    @LauncherNetworkAPI
    public final boolean allow;

    public JoinServerRequestEvent(boolean allow) {
        this.allow = allow;
    }

    @Override
    public String getType() {
        return "joinServer";
    }
}
