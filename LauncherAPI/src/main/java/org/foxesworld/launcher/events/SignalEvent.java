package org.foxesworld.launcher.events;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.request.WebSocketEvent;

//Используется, что бы послать короткое сообщение, которое вмещается в int
public class SignalEvent implements WebSocketEvent {
    @LauncherNetworkAPI
    public final int signal;

    public SignalEvent(int signal) {
        this.signal = signal;
    }

    @Override
    public String getType() {
        return "signal";
    }
}
