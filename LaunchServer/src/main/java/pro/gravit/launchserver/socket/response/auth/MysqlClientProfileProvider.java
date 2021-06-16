package pro.gravit.launchserver.socket.response.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pro.gravit.launcher.profiles.ClientProfile;
import pro.gravit.launchserver.LaunchServer;
import pro.gravit.launchserver.auth.MySQLSourceConfig;

public class MysqlClientProfileProvider extends ClientProfileProvider {

  private transient final Logger logger = LogManager.getLogger();
  public MySQLSourceConfig mySQLHolder;
  public String tableServers;
  public String columnName;
  public String columnServerAddress;
  public String columnServerPort;
  public String columnSrvImage;
  public String columnVersion;
  public String columnStory;
  public String columnSrvGroup;
  public String columnEnabled;
  public String columnClientArgs;
  public String columnMainClass;
  public String columnJvmArgs;
  private String sqlGetAll;

  public MysqlClientProfileProvider(MySQLSourceConfig mySQLHolder, String tableServers,
      String columnName, String columnServerAddress, String columnServerPort,
      String columnSrvImage, String columnVersion, String columnStory,
      String columnSrvGroup, String columnEnabled, String columnClientArgs,
      String columnMainClass, String columnJvmArgs) {
    this.mySQLHolder = mySQLHolder;
    this.tableServers = tableServers;
    this.columnName = columnName;
    this.columnServerAddress = columnServerAddress;
    this.columnServerPort = columnServerPort;
    this.columnSrvImage = columnSrvImage;
    this.columnVersion = columnVersion;
    this.columnStory = columnStory;
    this.columnSrvGroup = columnSrvGroup;
    this.columnEnabled = columnEnabled;
    this.columnClientArgs = columnClientArgs;
    this.columnMainClass = columnMainClass;
    this.columnJvmArgs = columnJvmArgs;
  }

  public MysqlClientProfileProvider() {
  }

  public static MysqlClientProfileProvider createDefault() {
    MySQLSourceConfig mySQLSourceConfig = new MySQLSourceConfig("poolName", "address", 3306,
        "username", "password", "database");
    return new MysqlClientProfileProvider(mySQLSourceConfig, "tableServers", "columnName",
        "columnServerAddress", "columnServerPort", "columnSrvImage", "columnVersion", "columnStory",
        "columnSrvGroup", "columnEnabled", "columnClientArgs", "columnMainClass", "columnJvmArgs");
  }

  public List<ClientProfile> getAll() {
    List<ClientProfile> clientProfileList = new ArrayList<>();
    try (Connection connection = mySQLHolder.getConnection()) {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlGetAll);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        String name = resultSet.getString(columnName);
        String serverAddress = resultSet.getString(columnServerAddress);
        int serverPort = resultSet.getInt(columnServerPort);
        String serverImage = resultSet.getString(columnSrvImage);
        String version = resultSet.getString(columnVersion);
        String story = resultSet.getString(columnStory);
        int serverGroup = resultSet.getInt(columnSrvGroup);
        boolean enabled = resultSet.getBoolean(columnEnabled);
        String clientArgs = resultSet.getString(columnClientArgs);
        String mainClass = resultSet.getString(columnMainClass);
        String jvmArgs = resultSet.getString(columnJvmArgs);
        ClientProfile clientProfile = new ClientProfile(name, serverAddress, serverPort, serverImage,
            version, story, serverGroup, enabled, clientArgs, mainClass, jvmArgs);
        clientProfileList.add(clientProfile);
      }
    } catch (SQLException e) {
      throw new IllegalStateException(e);
    }

    return clientProfileList;
  }

  public void init(LaunchServer launchServer) {
    sqlGetAll = String.format("SELECT * FROM %s WHERE %s = true", tableServers, true);
  }

  public void close() {
    mySQLHolder.close();
  }
}
