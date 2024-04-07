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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DbH2Creator {

  private final AuthorizationData authorizationData;
  private final AuthorizationSaver authorizationSaver;

//  private String dbName = "HellDB";
//  private String dbUrl = "jdbc:h2:file:./db/" + dbName + ";DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE";
//  private String username = "ODMEN";
//  private String password = "ADMIN";
//
//  public void ensureExistsDatabase(String authorizationFilePath) {
//    try {
//      Connection connection = DriverManager.getConnection(dbUrl, username, password);
//      connection.close();
//
//      saveAuthorizationDatabase(authorizationFilePath);
//      log.info("Successfully ensured the existence of the H2 database.");
//    } catch (SQLException e) {
//      log.error("Error with ensured embeded database: " + e);
//    }
//  }
//
//  private void saveAuthorizationDatabase(String filePath) {
//    Database database = new Database(true, DatabaseVendor.H2, dbName, dbUrl, username, password);
//
//    authorizationData.setDatabase(database);
//    authorizationSaver.saveToFile(filePath);
//    log.info("Authorization data (DatabaseVendor.H2) is successfully updated along the path: " + filePath);
//  }

  /////////////////

//  private Server server;

  private String dbNameS = "HellDB";
  private String dbUrlS = "jdbc:h2:tcp://localhost:10097/~/git/hell-player/Hell Player/db/" + dbNameS;
  private String usernameS = "ODMEN";
  private String passwordS = "ADMIN";

//  @PostConstruct
//  public void startDatabaseServer() {
//    try {
//      server = Server.createTcpServer("-tcpPort", "10099", "-tcpAllowOthers", "-tcpBindAddress", "127.0.0.1").start();
//      log.info("H2 database server started and available at: " + server.getURL());
//    } catch (SQLException e) {
//      log.error("Could not start H2 database server: " + e.getMessage(), e);
//    }
//  }

  public void ensureExistsDatabase(String authorizationFilePath) {
    try {
      Connection connection = DriverManager.getConnection(dbUrlS, usernameS, passwordS);
      connection.close();
      
      saveAuthorizationDatabaseS(authorizationFilePath);
      log.info("Successfully ensured the existence of the H2 database.");
    } catch (SQLException e) {
      log.error("Error with ensured embedded database: " + e);
    }
  }

//  public void stopDatabaseServer() {
//    if (server != null) {
//      server.stop();
//      log.info("H2 database server stopped.");
//    }
//  }
  
  private void saveAuthorizationDatabaseS(String filePath) {
    Database database = new Database(true, DatabaseVendor.H2, dbNameS, dbUrlS, usernameS, passwordS);

    authorizationData.setDatabase(database);
    authorizationSaver.saveToFile(filePath);
    log.info("Authorization data (DatabaseVendor.H2) is successfully updated along the path: " + filePath);
  }
}
