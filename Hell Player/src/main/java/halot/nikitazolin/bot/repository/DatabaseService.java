package halot.nikitazolin.bot.repository;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.DatabaseVendor;
import halot.nikitazolin.bot.repository.prepare.DataSourceRegistrator;
import halot.nikitazolin.bot.repository.prepare.DbH2Creator;
import halot.nikitazolin.bot.repository.prepare.FlywayMigrationRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseService {

  private final AuthorizationData authorizationData;
  private final DataSourceRegistrator dataSourceRegistrator;
  private final DbH2Creator dbH2Creator;
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
      dbH2Creator.ensureExistsDatabase(filePath);
    }

    dataSourceRegistrator.registerDataSourceСonstant();
    flywayMigrationRunner.migrateDatabaseConstant();
    log.info("Constant database was prepared");
  }

  private void prepareTemporaryDb() {
    flywayMigrationRunner.migrateDatabaseTemporary();
    log.info("Temporary database was prepared");
  }
}
