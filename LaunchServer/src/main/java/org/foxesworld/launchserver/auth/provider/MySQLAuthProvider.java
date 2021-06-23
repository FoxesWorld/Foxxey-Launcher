package org.foxesworld.launchserver.auth.provider;

import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.password.AuthPlainPassword;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.auth.MySQLSourceConfig;
import org.foxesworld.utils.helper.CommonHelper;
import org.foxesworld.utils.helper.SecurityHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class MySQLAuthProvider extends AuthProvider {
    private MySQLSourceConfig mySQLHolder;
    private String query;
    private String message;
    private String[] queryParams;
    private boolean flagsEnabled;

    @Override
    public void init(LaunchServer srv) {
        super.init(srv);
        if (query == null) throw new RuntimeException("[Verify][AuthProvider] query cannot be null");
        if (message == null) throw new RuntimeException("[Verify][AuthProvider] message cannot be null");
        if (mySQLHolder == null) throw new RuntimeException("[Verify][AuthProvider] mySQLHolder cannot be null");
    }

    @Override
    public AuthProviderResult auth(String login, AuthRequest.AuthPasswordInterface password, String ip, String hwid) throws SQLException, AuthException {
        if (!(password instanceof AuthPlainPassword)) throw new AuthException("This password type not supported");
        try (Connection c = mySQLHolder.getConnection()) {
            PreparedStatement s = c.prepareStatement(query);
            String[] replaceParams = {"login", login, "password", ((AuthPlainPassword) password).password, "ip", ip};
            for (int i = 0; i < queryParams.length; i++)
                s.setString(i + 1, CommonHelper.replace(queryParams[i], replaceParams));

            // Execute SQL query
            s.setQueryTimeout(MySQLSourceConfig.TIMEOUT);
            try (ResultSet set = s.executeQuery()) {
                return set.next() ? new AuthProviderResult(set.getString(1), SecurityHelper.randomStringToken(), new ClientPermissions(
                        set.getLong(2), flagsEnabled ? set.getLong(3) : 0), 0, 4) : authError(message);
            }
        }

    }

    @Override
    public void close() {
        mySQLHolder.close();
    }

    @SuppressWarnings("unused")
    public void setMySQLHolder(MySQLSourceConfig mySQLHolder) {
        this.mySQLHolder = mySQLHolder;
    }

    @SuppressWarnings("unused")
    public void setQuery(String query) {
        this.query = query;
    }

    @SuppressWarnings("unused")
    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public void setQueryParams(String[] queryParams) {
        this.queryParams = queryParams;
    }

    @SuppressWarnings("unused")
    public void setFlagsEnabled(boolean flagsEnabled) {
        this.flagsEnabled = flagsEnabled;
    }
}
