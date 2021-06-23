package org.foxesworld.launcher.client.gui.impl;

import org.foxesworld.launcher.client.gui.JavaFXApplication;
import org.foxesworld.launcher.client.gui.scenes.login.LoginScene;
import org.foxesworld.launcher.events.NotificationEvent;
import org.foxesworld.launcher.events.RequestEvent;
import org.foxesworld.launcher.events.request.AuthRequestEvent;
import org.foxesworld.launcher.request.WebSocketEvent;
import org.foxesworld.launcher.request.websockets.ClientWebSocketService;
import org.foxesworld.utils.helper.LogHelper;

public class GuiEventHandler implements ClientWebSocketService.EventHandler {
    private final JavaFXApplication application;

    public GuiEventHandler(JavaFXApplication application) {
        this.application = application;
    }

    @Override
    public <T extends WebSocketEvent> boolean eventHandle(T event) {
        LogHelper.dev("Processing event %s", event.getType());
        if( event instanceof RequestEvent)
        {
            if(!((RequestEvent) event).requestUUID.equals(RequestEvent.eventUUID))
                return false;
        }
        try {
            if (event instanceof NotificationEvent) {
                NotificationEvent e = (NotificationEvent) event;
                ContextHelper.runInFxThreadStatic(() -> application.messageManager.createNotification(e.head, e.message));
            } else if (event instanceof AuthRequestEvent) {
                boolean isNextScene = application.getCurrentScene() instanceof LoginScene;
                LogHelper.dev("Receive auth event. Send next scene %s", isNextScene ? "true" : "false");
                application.stateService.setAuthResult(null, (AuthRequestEvent) event);
                if (isNextScene && ((LoginScene) application.getCurrentScene()).isLoginStarted)
                    ((LoginScene) application.getCurrentScene()).onGetProfiles();
            }
        } catch (Throwable e) {
            LogHelper.error(e);
        }
        return false;
    }
}
