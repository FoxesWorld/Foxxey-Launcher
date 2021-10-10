package org.foxesworld.launchserver.socket.response.auth;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launcher.events.request.ProfilesRequestEvent;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launchserver.auth.protect.interfaces.ProfilesProtectHandler;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfilesResponse extends SimpleResponse {
    @Override
    public String getType() {
        return "profiles";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        if (server.config.protectHandler instanceof ProfilesProtectHandler && !((ProfilesProtectHandler) server.config.protectHandler).canGetProfiles(client)) {
            sendError("Access denied");
            return;
        }

        List<ClientProfile> profileList;
        Set<ClientProfile> serverProfiles = server.getProfiles();
        if (server.config.protectHandler instanceof ProfilesProtectHandler protectHandler) {
            profileList = new ArrayList<>(4);
            for (ClientProfile profile : serverProfiles) {
                if (protectHandler.canGetProfile(profile, client)) {
                    if (profile.getClientGroup() == client.groupId || profile.getClientGroup() == 4) {
                        profileList.add(profile);
                    }
                }
            }
        } else {
            profileList = serverProfiles
                    .stream()
                    .filter(clientProfile -> clientProfile.getClientGroup() == client.groupId && clientProfile.getClientGroup() == 4)
                    .collect(Collectors.toList());
        }
        sendResult(new ProfilesRequestEvent(profileList));
    }
}
