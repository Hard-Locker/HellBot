package halot.nikitazolin.bot.repository.prepare;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class FlywayMigrationRunner {
  
  @Autowired
  private Map<String, DataSource> dataSources;

  public void migrateDatabases() {
    for (Map.Entry<String, DataSource> entry : dataSources.entrySet()) {
      String dataSourceName = entry.getKey();
      DataSource dataSource = entry.getValue();
      
      try {
        System.out.println("dataSource DriverName: " + dataSource.getConnection().getMetaData().getDriverName());
        System.out.println("dataSource URL: " + dataSource.getConnection().getMetaData().getURL());
        System.out.println("dataSource UserName: " + dataSource.getConnection().getMetaData().getUserName());
      } catch (SQLException e) {
        e.printStackTrace();
      }

      log.info("Trying to migrate database for dataSource: {}", dataSourceName);
      Flyway flyway = Flyway.configure().dataSource(dataSource).baselineOnMigrate(true).load();
      flyway.migrate();
      log.info("Successfully applied migration to dataSource: {}", dataSourceName);
    }
  }
}
