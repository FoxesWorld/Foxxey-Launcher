package org.foxesworld.launchserver.auth.core.interfaces.provider;

import org.foxesworld.launchserver.auth.Feature;
import org.foxesworld.launchserver.auth.core.User;
import org.foxesworld.launchserver.auth.core.UserSession;

import java.util.List;

@Feature("sessions")
@SuppressWarnings("unused")
public interface AuthSupportGetSessionsFromUser extends AuthSupport {
    List<UserSession> getSessionsByUser(User user);

    void clearSessionsByUser(User user);
}
