package org.foxesworld.launchserver.auth.provider;

import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.password.AuthPlainPassword;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.auth.RequiredDAO;
import org.foxesworld.launchserver.dao.User;
import org.foxesworld.launchserver.manangers.hook.AuthHookManager;
import org.foxesworld.utils.helper.SecurityHelper;

public class HibernateAuthProvider extends AuthProvider implements RequiredDAO {
    public boolean autoReg;

    @Override
    public AuthProviderResult auth(String login, AuthRequest.AuthPasswordInterface password, String ip, String hwid) throws Exception {
        if (!(password instanceof AuthPlainPassword)) throw new AuthException("This password type not supported");
        User user = srv.config.dao.userDAO.findByUsername(login);
        if (user == null && autoReg) {
            AuthHookManager.RegContext context = new AuthHookManager.RegContext(login, ((AuthPlainPassword) password).password, ip, false);
            if (!srv.authHookManager.registraion.hook(context)) {
                throw new AuthException("Registration canceled. Try again later");
            }
        }
        if (user == null || !user.verifyPassword(((AuthPlainPassword) password).password)) {
            if (user == null) throw new AuthException("Username incorrect");
            else throw new AuthException("Username or password incorrect");
        }
        return new AuthProviderDAOResult(user.getUsername(), SecurityHelper.randomStringToken(), user.getPermissions(), user);
    }

    @Override
    public void close() {

    }
}
