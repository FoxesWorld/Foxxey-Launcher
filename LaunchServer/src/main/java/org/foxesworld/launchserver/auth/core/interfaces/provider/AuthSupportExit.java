package org.foxesworld.launchserver.auth.core.interfaces.provider;

import org.foxesworld.launchserver.auth.core.User;
import org.foxesworld.launchserver.auth.core.UserSession;

@SuppressWarnings("unused")
public interface AuthSupportExit extends AuthSupport {
    boolean deleteSession(UserSession session);

    boolean exitUser(User user);
}
