package org.foxesworld.launchserver.auth.handler;

import org.foxesworld.launchserver.auth.RequiredDAO;
import org.foxesworld.launchserver.dao.User;

import java.util.UUID;

public class HibernateAuthHandler extends CachedAuthHandler implements RequiredDAO {
    @Override
    protected Entry fetchEntry(String username) {
        User user = srv.config.dao.userDAO.findByUsername(username);
        if (user == null) return null;
        return new Entry(user.getUuid(), user.getUsername(), user.getAccessToken(), user.getServerID());
    }

    @Override
    protected Entry fetchEntry(UUID uuid) {
        User user = srv.config.dao.userDAO.findByUUID(uuid);
        if (user == null) return null;
        return new Entry(user.getUuid(), user.getUsername(), user.getAccessToken(), user.getServerID());
    }

    @Override
    protected boolean updateAuth(UUID uuid, String username, String accessToken) {
        User user = srv.config.dao.userDAO.findByUUID(uuid);
        user.setAccessToken(accessToken);
        srv.config.dao.userDAO.update(user);
        return true;
    }

    @Override
    protected boolean updateServerID(UUID uuid, String serverID) {
        User user = srv.config.dao.userDAO.findByUUID(uuid);
        user.setServerID(serverID);
        srv.config.dao.userDAO.update(user);
        return true;
    }

    @Override
    public void close() {

    }
}
