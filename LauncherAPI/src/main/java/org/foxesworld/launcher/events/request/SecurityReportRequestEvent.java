package org.foxesworld.launcher.events.request;

import org.foxesworld.launcher.events.RequestEvent;

public class SecurityReportRequestEvent extends RequestEvent {
    public final ReportAction action;
    public final String otherAction;

    public SecurityReportRequestEvent(ReportAction action) {
        this.action = action;
        this.otherAction = null;
    }

    public SecurityReportRequestEvent(String otherAction) {
        this.action = ReportAction.OTHER;
        this.otherAction = otherAction;
    }

    public SecurityReportRequestEvent() {
        this.action = ReportAction.NONE;
        this.otherAction = null;
    }

    @Override
    public String getType() {
        return "securityReport";
    }

    public enum ReportAction {
        NONE,
        LOGOUT,
        EXIT,
        CRASH,
        OTHER
    }
}
