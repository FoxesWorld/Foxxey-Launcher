package org.foxesworld.launchserver.auth;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.utils.helper.VerifyHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class MySQLSourceConfig implements AutoCloseable {

    public static final int TIMEOUT = VerifyHelper.verifyInt(
            Integer.parseUnsignedInt(System.getProperty("launcher.mysql.idleTimeout", Integer.toString(5000))),
            VerifyHelper.POSITIVE, "launcher.mysql.idleTimeout can't be <= 5000");
    private static final int MAX_POOL_SIZE = VerifyHelper.verifyInt(
            Integer.parseUnsignedInt(System.getProperty("launcher.mysql.maxPoolSize", Integer.toString(3))),
            VerifyHelper.POSITIVE, "launcher.mysql.maxPoolSize can't be <= 0");

    // Instance
    private transient final String poolName;
    private transient final Logger logger = LogManager.getLogger();

    // Config
    private String address;
    private int port;
    @SuppressWarnings("unused")
    private boolean useSSL;
    @SuppressWarnings("unused")
    private boolean verifyCertificates;
    private String username;
    private String password;
    private String database;
    private String timezone;
    private boolean useHikari;

    // Cache
    private transient DataSource source;
    private transient boolean hikari;

    @SuppressWarnings("unused")
    public MySQLSourceConfig(String poolName) {
        this.poolName = poolName;
    }

    @SuppressWarnings("unused")
    public MySQLSourceConfig(String poolName, String address, int port, String username, String password, String database) {
        this.poolName = poolName;
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @SuppressWarnings("unused")
    public MySQLSourceConfig(String poolName, DataSource source, boolean hikari) {
        this.poolName = poolName;
        this.source = source;
        this.hikari = hikari;
    }

    @Override
    public synchronized void close() {
        if (hikari)
            ((HikariDataSource) source).close();
    }


    public synchronized Connection getConnection() throws SQLException {
        if (source == null) { // New data source
            MysqlDataSource mysqlSource = new MysqlDataSource();
            mysqlSource.setCharacterEncoding("UTF-8");

            // Prep statements cache
            mysqlSource.setPrepStmtCacheSize(250);
            mysqlSource.setPrepStmtCacheSqlLimit(2048);
            mysqlSource.setCachePrepStmts(true);
            mysqlSource.setUseServerPrepStmts(true);

            // General optimizations
            mysqlSource.setCacheServerConfiguration(true);
            mysqlSource.setUseLocalSessionState(true);
            mysqlSource.setRewriteBatchedStatements(true);
            mysqlSource.setMaintainTimeStats(false);
            mysqlSource.setUseUnbufferedInput(false);
            mysqlSource.setUseReadAheadInput(false);
            mysqlSource.setUseSSL(useSSL);
            mysqlSource.setVerifyServerCertificate(verifyCertificates);
            // Set credentials
            mysqlSource.setServerName(address);
            mysqlSource.setPortNumber(port);
            mysqlSource.setUser(username);
            mysqlSource.setPassword(password);
            mysqlSource.setDatabaseName(database);
            mysqlSource.setTcpNoDelay(true);
            if (timezone != null) mysqlSource.setServerTimezone(timezone);
            hikari = false;
            // Try using HikariCP
            source = mysqlSource;
            if (useHikari) {
                try {
                    Class.forName("com.zaxxer.hikari.HikariDataSource");
                    hikari = true; // Used for shutdown. Not instanceof because of possible classpath error
                    HikariConfig hikariConfig = new HikariConfig();
                    hikariConfig.setDataSource(mysqlSource);
                    hikariConfig.setPoolName(poolName);
                    hikariConfig.setMinimumIdle(1);
                    hikariConfig.setMaximumPoolSize(MAX_POOL_SIZE);
                    hikariConfig.setConnectionTestQuery("SELECT 1");
                    hikariConfig.setConnectionTimeout(1000);
                    hikariConfig.setAutoCommit(true);
                    hikariConfig.setLeakDetectionThreshold(2000);
                    // Set HikariCP pool
                    // Replace source with hds
                    source = new HikariDataSource(hikariConfig);
                } catch (ClassNotFoundException ignored) {
                    logger.debug("HikariCP isn't in classpath for '{}'", poolName);
                }
            }

        }
        return source.getConnection();
    }
}
