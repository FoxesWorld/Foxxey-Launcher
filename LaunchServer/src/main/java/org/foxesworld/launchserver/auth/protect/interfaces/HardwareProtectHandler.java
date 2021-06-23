package org.foxesworld.launchserver.auth.protect.interfaces;

import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.secure.HardwareReportResponse;

public interface HardwareProtectHandler {
    void onHardwareReport(HardwareReportResponse response, Client client);
}
