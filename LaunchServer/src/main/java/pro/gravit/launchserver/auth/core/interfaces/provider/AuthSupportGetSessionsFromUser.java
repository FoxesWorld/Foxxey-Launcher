package pro.gravit.launchserver.auth.core.interfaces.provider;

import pro.gravit.launchserver.auth.Feature;
import pro.gravit.launchserver.auth.core.User;
import pro.gravit.launchserver.auth.core.UserSession;

import java.util.List;

@Feature("sessions")
@SuppressWarnings("unused")
public interface AuthSupportGetSessionsFromUser extends AuthSupport {
    List<UserSession> getSessionsByUser(User user);

    void clearSessionsByUser(User user);
}
