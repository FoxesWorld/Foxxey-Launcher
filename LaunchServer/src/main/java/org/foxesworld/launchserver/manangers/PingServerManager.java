package org.foxesworld.launchserver.manangers;

import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launcher.request.management.PingServerReportRequest;
import org.foxesworld.launchserver.LaunchServer;

import java.util.HashMap;
import java.util.Map;

public class PingServerManager {
    public static final long REPORT_EXPIRED_TIME = 20 * 1000;
    public final Map<String, ServerInfoEntry> map = new HashMap<>();
    private final LaunchServer server;

    public PingServerManager(LaunchServer server) {
        this.server = server;
    }

    public void syncServers() {
        server.getProfiles().forEach((p) -> {
            for (ClientProfile.ServerProfile sp : p.getServers()) {
                ServerInfoEntry entry = map.get(sp.name);
                if (entry == null) {
                    map.put(sp.name, new ServerInfoEntry(p));
                }
            }
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean updateServer(String name, PingServerReportRequest.PingServerReport report) {
        ServerInfoEntry entry = map.get(name);
        if (entry == null)
            return false;
        else {
            entry.lastReportTime = System.currentTimeMillis();
            entry.lastReport = report;
            return true;
        }
    }

    public static class ServerInfoEntry {
        public final ClientProfile profile;
        public PingServerReportRequest.PingServerReport lastReport;
        public long lastReportTime;

        @SuppressWarnings("unused")
        public ServerInfoEntry(ClientProfile profile, PingServerReportRequest.PingServerReport lastReport) {
            this.lastReport = lastReport;
            this.profile = profile;
            this.lastReportTime = System.currentTimeMillis();
        }

        public ServerInfoEntry(ClientProfile profile) {
            this.profile = profile;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - lastReportTime > REPORT_EXPIRED_TIME;
        }
    }
}
