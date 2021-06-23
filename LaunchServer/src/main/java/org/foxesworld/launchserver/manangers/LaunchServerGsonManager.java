package org.foxesworld.launchserver.manangers;

import com.google.gson.GsonBuilder;
import org.foxesworld.launcher.events.request.GetAvailabilityAuthRequestEvent;
import org.foxesworld.launcher.managers.GsonManager;
import org.foxesworld.launcher.modules.events.PreGsonPhase;
import org.foxesworld.launcher.profiles.optional.actions.OptionalAction;
import org.foxesworld.launcher.profiles.optional.triggers.OptionalTrigger;
import org.foxesworld.launcher.request.JsonResultSerializeAdapter;
import org.foxesworld.launcher.request.WebSocketEvent;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.GetAvailabilityAuthRequest;
import org.foxesworld.launchserver.auth.core.AuthCoreProvider;
import org.foxesworld.launchserver.auth.handler.AuthHandler;
import org.foxesworld.launchserver.auth.password.PasswordVerifier;
import org.foxesworld.launchserver.auth.protect.ProtectHandler;
import org.foxesworld.launchserver.auth.protect.hwid.HWIDProvider;
import org.foxesworld.launchserver.auth.provider.AuthProvider;
import org.foxesworld.launchserver.auth.session.SessionStorage;
import org.foxesworld.launchserver.auth.texture.TextureProvider;
import org.foxesworld.launchserver.components.Component;
import org.foxesworld.launchserver.dao.provider.DaoProvider;
import org.foxesworld.launchserver.modules.impl.LaunchServerModulesManager;
import org.foxesworld.launchserver.news.NewsProvider;
import org.foxesworld.launchserver.socket.WebSocketService;
import org.foxesworld.launchserver.socket.response.UnknownResponse;
import org.foxesworld.launchserver.socket.response.WebSocketServerResponse;
import org.foxesworld.launchserver.client.ClientProfileProvider;
import org.foxesworld.utils.UniversalJsonAdapter;

public class LaunchServerGsonManager extends GsonManager {
    private final LaunchServerModulesManager modulesManager;

    public LaunchServerGsonManager(LaunchServerModulesManager modulesManager) {
        this.modulesManager = modulesManager;
    }

    @Override
    public void registerAdapters(GsonBuilder builder) {
        super.registerAdapters(builder);
        builder.registerTypeAdapter(AuthProvider.class, new UniversalJsonAdapter<>(AuthProvider.providers));
        builder.registerTypeAdapter(TextureProvider.class, new UniversalJsonAdapter<>(TextureProvider.providers));
        builder.registerTypeAdapter(AuthHandler.class, new UniversalJsonAdapter<>(AuthHandler.providers));
        builder.registerTypeAdapter(AuthCoreProvider.class, new UniversalJsonAdapter<>(AuthCoreProvider.providers));
        builder.registerTypeAdapter(PasswordVerifier.class, new UniversalJsonAdapter<>(PasswordVerifier.providers));
        builder.registerTypeAdapter(Component.class, new UniversalJsonAdapter<>(Component.providers));
        builder.registerTypeAdapter(ProtectHandler.class, new UniversalJsonAdapter<>(ProtectHandler.providers));
        builder.registerTypeAdapter(ClientProfileProvider.class, new UniversalJsonAdapter<>(ClientProfileProvider.providers));
        builder.registerTypeAdapter(NewsProvider.class, new UniversalJsonAdapter<>(NewsProvider.providers));
        builder.registerTypeAdapter(DaoProvider.class, new UniversalJsonAdapter<>(DaoProvider.providers));
        builder.registerTypeAdapter(WebSocketServerResponse.class, new UniversalJsonAdapter<>(WebSocketService.providers, UnknownResponse.class));
        builder.registerTypeAdapter(WebSocketEvent.class, new JsonResultSerializeAdapter());
        builder.registerTypeAdapter(AuthRequest.AuthPasswordInterface.class, new UniversalJsonAdapter<>(AuthRequest.providers));
        builder.registerTypeAdapter(GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails.class, new UniversalJsonAdapter<>(GetAvailabilityAuthRequest.providers));
        builder.registerTypeAdapter(HWIDProvider.class, new UniversalJsonAdapter<>(HWIDProvider.providers));
        builder.registerTypeAdapter(OptionalAction.class, new UniversalJsonAdapter<>(OptionalAction.providers));
        builder.registerTypeAdapter(OptionalTrigger.class, new UniversalJsonAdapter<>(OptionalTrigger.providers));
        builder.registerTypeAdapter(SessionStorage.class, new UniversalJsonAdapter<>(SessionStorage.providers));
        modulesManager.invokeEvent(new PreGsonPhase(builder));
    }
}
