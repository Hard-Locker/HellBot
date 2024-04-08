package halot.nikitazolin.bot.repository.prepare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

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

  public boolean checkAuthorizationData() {
    Database db = authorizationData.getDatabase();

    return db != null && db.getDbName() != null && db.getDbUrl() != null && db.getDbUsername() != null
        && db.getDbPassword() != null;
  }

  public void createDefaultDatabase(String authorizationFilePath) {
    String dbName = "HellDB";
    String dbPath = "/./db/" + dbName;
    String port = "10097";
    String dbUrl = "jdbc:h2:tcp://localhost:" + port + dbPath;
    String username = "ODMEN";
    String password = "ADMIN";

    startServer(port);

    try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
      Database database = new Database(true, DatabaseVendor.H2, dbName, dbUrl, username, password);

      saveAuthorizationDatabase(authorizationFilePath, database);
      log.info("Database created with default data.");
    } catch (SQLException e) {
      log.error("Error while creating the database: " + e.getMessage(), e);
    }
  }

  public void startDatabaseServer() {
    if (!checkAuthorizationData()) {
      return;
    }

    try {
      String dbUrl = authorizationData.getDatabase().getDbUrl();
      Optional<String> portOptional = extractPortFromUrl(dbUrl);

      portOptional.ifPresentOrElse(this::startServer,
          () -> log.error("Port was not found or invalid in the database URL"));
    } catch (Exception e) {
      log.error("Error with starting database server: {}", e.getMessage(), e);
    }
  }

  private Optional<String> extractPortFromUrl(String dbUrl) {
    try {
      int start = dbUrl.indexOf("tcp://") + 6;
      int end = dbUrl.indexOf("/", start);

      if (end == -1) {
        end = dbUrl.length();
      }

      String hostAndPort = dbUrl.substring(start, end);
      String[] parts = hostAndPort.split(":");

      if (parts.length > 1 && !parts[1].isEmpty() && parts[1].matches("\\d{4,5}")) {
        log.info("Successfully found port for server: {}", parts[1]);

        return Optional.of(parts[1]);
      }
    } catch (Exception e) {
      log.error("Error while searching for server port: {}", e.getMessage(), e);
    }

    return Optional.empty();
  }

  private void startServer(String port) {
    try {
      server = Server.createTcpServer("-tcpPort", port, "-tcpAllowOthers", "-ifNotExists").start();
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

  private void saveAuthorizationDatabase(String authorizationFilePath, Database database) {
    authorizationData.setDatabase(database);
    authorizationSaver.saveToFile(authorizationFilePath);
    log.info("Authorization data (DatabaseVendor.H2) is successfully updated along the path: " + authorizationFilePath);
  }
}
