package org.foxesworld.launcher.server;

import com.google.gson.GsonBuilder;
import org.foxesworld.launcher.managers.GsonManager;
import org.foxesworld.launcher.modules.events.PreGsonPhase;
import org.foxesworld.launcher.request.websockets.ClientWebSocketService;

public class ServerWrapperGsonManager extends GsonManager {
    private final ServerWrapperModulesManager modulesManager;

    public ServerWrapperGsonManager(ServerWrapperModulesManager modulesManager) {
        this.modulesManager = modulesManager;
    }

    @Override
    public void registerAdapters(GsonBuilder builder) {
        super.registerAdapters(builder);
        ClientWebSocketService.appendTypeAdapters(builder);
        modulesManager.invokeEvent(new PreGsonPhase(builder));
    }
}
