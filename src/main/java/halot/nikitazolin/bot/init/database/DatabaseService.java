package halot.nikitazolin.bot.init.database;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.DatabaseVendor;
import halot.nikitazolin.bot.init.database.manager.DbH2Manager;
import halot.nikitazolin.bot.init.database.manager.DbRegistrator;
import halot.nikitazolin.bot.init.database.manager.FlywayMigrationRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseService {

  private final AuthorizationData authorizationData;
  private final DbRegistrator dbRegistrator;
  private final DbH2Manager dbH2Manager;
  private final FlywayMigrationRunner flywayMigrationRunner;

  public void validateDb(String filePath) {
    selectDb(filePath);
  }

  private void selectDb(String filePath) {
    if (authorizationData.getDatabase().isDbEnabled() == true) {
      prepareConstantDb(filePath);
      log.info("Select constant database");
    } else {
      prepareTemporaryDb();
      log.info("Select temporary database");
    }
  }

  private void prepareConstantDb(String filePath) {
    if (authorizationData.getDatabase().getDbVendor() == DatabaseVendor.H2) {
      log.info("Ensure exists H2 database");

      if (dbH2Manager.checkAuthorizationData()) {
        log.info("Authorization data have information. Try start database");
        dbH2Manager.startDatabaseServer();
      } else {
        log.info("Authorization data is invalid. Create default database");
        dbH2Manager.createDefaultDatabase(filePath);
      }
    }

    dbRegistrator.registerConstantDb();
    flywayMigrationRunner.migrateDatabases();
    log.info("Constant database was prepared");
  }

  private void prepareTemporaryDb() {
    flywayMigrationRunner.migrateDatabases();
    log.info("Temporary database was prepared");
  }
}
