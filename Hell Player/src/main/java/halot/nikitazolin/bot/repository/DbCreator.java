package halot.nikitazolin.bot.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DbCreator {

  private String dbUrl = "jdbc:h2:file:./db/HellDB;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE";
  private String user = "odmen";
  private String password = "admin";

  public void createDatabase() {
    try {
      Connection connection = DriverManager.getConnection(dbUrl, user, password);

      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  public void createTableSQL(Connection connection) {
    String resourcePath = "db/migration/V1__CreateTable.sql";
  }
}
