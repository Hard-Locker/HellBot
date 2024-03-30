package halot.nikitazolin.bot.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.init.authorization.AuthorizationSaver;
import halot.nikitazolin.bot.init.authorization.data.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.data.Database;
import halot.nikitazolin.bot.init.authorization.data.DatabaseVendor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DbCreator {

  private final AuthorizationData authorizationData;
  private final AuthorizationSaver authorizationSaver;

  private String dbName = "HellDB";
  private String dbUrl = "jdbc:h2:file:./db/" + dbName + ";DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE";
  private String username = "odmen";
  private String password = "admin";

  public void createDatabase(String authorizationFilePath) {
    try {
      Connection connection = DriverManager.getConnection(dbUrl, username, password);
      saveAuthorizationDatabase(authorizationFilePath);
      
      connection.close();
      
      log.info("Successfully create H2 database");
    } catch (SQLException e) {
      log.error("Error with create embeded database: " + e);
    }
  }

  private void saveAuthorizationDatabase(String filePath) {
    Database database = new Database(true, DatabaseVendor.H2, dbName, dbUrl, username, password);

    authorizationData.setDatabase(database);
    authorizationSaver.saveToFile(filePath);
    log.info("Authorization data (Database) save successfully");
  }

  public void createTableSQL(Connection connection) {
    String resourcePath = "db/migration/V1__CreateTable.sql";
  }
}
