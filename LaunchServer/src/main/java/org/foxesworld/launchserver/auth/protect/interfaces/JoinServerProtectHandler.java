package org.foxesworld.launchserver.auth.protect.interfaces;

import org.foxesworld.launchserver.socket.Client;

public interface JoinServerProtectHandler {
    default boolean onJoinServer(String serverID, String username, Client client) {
        return true;
    }
}
