package org.foxesworld.launchserver.auth.core.interfaces.provider;

import org.foxesworld.launchserver.auth.Feature;
import org.foxesworld.launchserver.auth.core.User;
import org.foxesworld.launchserver.auth.core.interfaces.user.UserSupportMoney;

@Feature("money")
@SuppressWarnings("unused")
public interface AuthSupportMoney extends AuthSupport {
    default UserSupportMoney fetchUserMoney(User user) {
        return (UserSupportMoney) user;
    }
}
