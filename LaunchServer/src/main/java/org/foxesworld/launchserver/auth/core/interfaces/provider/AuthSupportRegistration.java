package org.foxesworld.launchserver.auth.core.interfaces.provider;

import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launchserver.auth.Feature;
import org.foxesworld.launchserver.auth.core.User;

import java.util.Map;

@Feature("registration")
@SuppressWarnings("unused")
public interface AuthSupportRegistration extends AuthSupport {
    User registration(String login, String email, AuthRequest.AuthPasswordInterface password, Map<String, String> properties);
}
