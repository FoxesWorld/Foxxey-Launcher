package org.foxesworld.launcher.request.auth.password;

import org.foxesworld.launcher.request.auth.AuthRequest;

public class AuthTOTPPassword implements AuthRequest.AuthPasswordInterface {
    public String totp;

    @Override
    public boolean check() {
        return true;
    }
}
