package org.foxesworld.launcher.api;

import org.foxesworld.launcher.LauncherEngine;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.utils.helper.LogHelper;

public class SystemService {
    private SystemService() {
        throw new UnsupportedOperationException();
    }

    public static void exit(int code) {
        LauncherEngine.exitLauncher(code);
    }

    public static void setSecurityManager(SecurityManager s) {
        LogHelper.debug("Try set security manager %s", s == null ? "null" : s.getClass().getName());
        if (AuthService.profile == null || AuthService.profile.getSecurityManagerConfig() == ClientProfile.SecurityManagerConfig.NONE)
            return;
        if (AuthService.profile.getSecurityManagerConfig() == ClientProfile.SecurityManagerConfig.CLIENT) {
            System.setSecurityManager(s);
        }
        //TODO NEXT
    }
}
