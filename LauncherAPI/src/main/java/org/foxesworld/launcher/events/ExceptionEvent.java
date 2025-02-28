package org.foxesworld.launcher.events;

import org.foxesworld.launcher.LauncherNetworkAPI;

public class ExceptionEvent extends RequestEvent {
    @LauncherNetworkAPI
    public final String message;
    @LauncherNetworkAPI
    public final String clazz;

    public ExceptionEvent(Exception e) {
        this.message = e.getMessage();
        this.clazz = e.getClass().getName();
    }

    @Override
    public String getType() {
        return "exception";
    }
}
