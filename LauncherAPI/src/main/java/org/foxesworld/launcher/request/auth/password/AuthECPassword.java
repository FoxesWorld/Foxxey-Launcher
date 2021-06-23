package org.foxesworld.launcher.request.auth.password;

import org.foxesworld.launcher.LauncherNetworkAPI;
import org.foxesworld.launcher.request.auth.AuthRequest;

@Deprecated
public class AuthECPassword implements AuthRequest.AuthPasswordInterface {
    @LauncherNetworkAPI
    public final byte[] password;

    public AuthECPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public boolean check() {
        return true;
    }
}
