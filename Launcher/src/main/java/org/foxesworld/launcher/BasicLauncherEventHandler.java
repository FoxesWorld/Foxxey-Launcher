package org.foxesworld.launcher;

import org.foxesworld.launcher.api.DialogService;
import org.foxesworld.launcher.events.ExtendedTokenRequestEvent;
import org.foxesworld.launcher.events.NotificationEvent;
import org.foxesworld.launcher.events.request.SecurityReportRequestEvent;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.WebSocketEvent;
import org.foxesworld.launcher.request.websockets.ClientWebSocketService;

public class BasicLauncherEventHandler implements ClientWebSocketService.EventHandler {

    @Override
    public <T extends WebSocketEvent> boolean eventHandle(T event) {
        if (event instanceof SecurityReportRequestEvent) {
            SecurityReportRequestEvent event1 = (SecurityReportRequestEvent) event;
            if (event1.action == SecurityReportRequestEvent.ReportAction.CRASH) {
                LauncherEngine.exitLauncher(80);
            }
        }
        if (event instanceof ExtendedTokenRequestEvent) {
            ExtendedTokenRequestEvent event1 = (ExtendedTokenRequestEvent) event;
            String token = event1.getExtendedToken();
            if (token != null) {
                Request.addExtendedToken(event1.getExtendedTokenName(), token);
            }
        } else if (event instanceof NotificationEvent) {
            NotificationEvent n = (NotificationEvent) event;
            if (DialogService.isNotificationsAvailable()) {
                DialogService.createNotification(n.icon, n.head, n.message);
            }
        }
        return false;
    }
}
