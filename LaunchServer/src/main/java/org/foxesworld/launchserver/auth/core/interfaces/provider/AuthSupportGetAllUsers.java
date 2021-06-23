package org.foxesworld.launchserver.auth.core.interfaces.provider;

import org.foxesworld.launchserver.auth.Feature;
import org.foxesworld.launchserver.auth.core.User;

@Feature("users")
public interface AuthSupportGetAllUsers extends AuthSupport {
    Iterable<User> getAllUsers();
}
