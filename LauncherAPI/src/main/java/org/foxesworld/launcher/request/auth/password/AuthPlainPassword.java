package org.foxesworld.launcher.request.auth.password;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.request.auth.AuthRequest;

public class AuthPlainPassword implements AuthRequest.AuthPasswordInterface {
    @LauncherNetworkAPI
    public final String password;

    public AuthPlainPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean check() {
        return true;
    }
}
