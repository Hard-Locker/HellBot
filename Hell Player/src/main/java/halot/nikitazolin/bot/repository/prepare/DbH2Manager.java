package halot.nikitazolin.bot.repository.prepare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.init.authorization.manager.AuthorizationSaver;
import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.Database;
import halot.nikitazolin.bot.init.authorization.model.DatabaseVendor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DbH2Manager {

  private final AuthorizationData authorizationData;
  private final AuthorizationSaver authorizationSaver;

  private Server server;
  private String dbName = "HellDB";
  private String dbUrl = "jdbc:h2:tcp://localhost:10097/~/git/hell-player/Hell Player/db/" + dbName;
  private String username = "ODMEN";
  private String password = "ADMIN";

  public void ensureExistsDatabase(String authorizationFilePath) {
    // TODO Need check and fill if empty
    Database database = authorizationData.getDatabase();

    startDatabaseServer();

    try {
      Connection connection = DriverManager.getConnection(dbUrl, username, password);
      connection.close();

      saveAuthorizationDatabase(authorizationFilePath);
      log.info("Successfully ensured the existence of the H2 database.");
    } catch (SQLException e) {
      log.error("Error with ensured embedded database: " + e);
    }
  }

  private void saveAuthorizationDatabase(String filePath) {
    Database database = new Database(true, DatabaseVendor.H2, dbName, dbUrl, username, password);

    authorizationData.setDatabase(database);
    authorizationSaver.saveToFile(filePath);
    log.info("Authorization data (DatabaseVendor.H2) is successfully updated along the path: " + filePath);
  }

  public void startDatabaseServer() {
    try {
      server = Server.createTcpServer("-tcpPort", "10097", "-tcpAllowOthers", "-ifNotExists").start();
      log.info("H2 database server started and available at: " + server.getURL());
    } catch (SQLException e) {
      log.error("Could not start H2 database server: " + e.getMessage(), e);
    }
  }

  public void stopDatabaseServer() {
    if (server != null) {
      server.stop();
      log.info("H2 database server stopped.");
    }
  }
}
