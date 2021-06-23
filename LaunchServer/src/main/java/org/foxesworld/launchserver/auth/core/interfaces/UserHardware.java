package org.foxesworld.launchserver.auth.core.interfaces;

import org.foxesworld.launcher.request.secure.HardwareReportRequest;

public interface UserHardware {
    HardwareReportRequest.HardwareInfo getHardwareInfo();

    byte[] getPublicKey();

    String getId();

    boolean isBanned();
}
