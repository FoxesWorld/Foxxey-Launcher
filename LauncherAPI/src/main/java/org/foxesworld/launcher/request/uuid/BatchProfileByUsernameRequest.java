package org.foxesworld.launcher.request.uuid;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.events.request.BatchProfileByUsernameRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.websockets.WebSocketRequest;
import org.foxesworld.utils.helper.IOHelper;
import org.foxesworld.utils.helper.VerifyHelper;

import java.io.IOException;

public final class BatchProfileByUsernameRequest extends Request<BatchProfileByUsernameRequestEvent> implements WebSocketRequest {
    @LauncherNetworkAPI
    public final Entry[] list;

    public BatchProfileByUsernameRequest(String... usernames) throws IOException {
        this.list = new Entry[usernames.length];
        for (int i = 0; i < usernames.length; ++i) {
            this.list[i] = new Entry();
            this.list[i].client = "";
            this.list[i].username = usernames[i];
        }
        IOHelper.verifyLength(usernames.length, IOHelper.MAX_BATCH_SIZE);
        for (String username : usernames)
            VerifyHelper.verifyUsername(username);
    }

    @Override
    public String getType() {
        return "batchProfileByUsername";
    }

    static class Entry {
        @LauncherNetworkAPI
        String username;
        @LauncherNetworkAPI
        String client;
    }
}
