package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.events.RequestEvent;

public class RegisterRequestEvent extends RequestEvent {
    @Override
    public String getType() {
        return "register";
    }
}
