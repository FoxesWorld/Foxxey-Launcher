package org.foxesworld.launcher.managers;

import com.google.gson.GsonBuilder;
import org.foxesworld.launcher.client.ClientModuleManager;
import org.foxesworld.launcher.client.UserSettings;
import org.foxesworld.launcher.modules.events.PreGsonPhase;
import org.foxesworld.launcher.request.websockets.ClientWebSocketService;
import org.foxesworld.utils.UniversalJsonAdapter;

public class ClientGsonManager extends GsonManager {
    private final ClientModuleManager moduleManager;

    public ClientGsonManager(ClientModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public void registerAdapters(GsonBuilder builder) {
        super.registerAdapters(builder);
        builder.registerTypeAdapter(UserSettings.class, new UniversalJsonAdapter<>(UserSettings.providers));
        ClientWebSocketService.appendTypeAdapters(builder);
        moduleManager.invokeEvent(new PreGsonPhase(builder));
    }
}
