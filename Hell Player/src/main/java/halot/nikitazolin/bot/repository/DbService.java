package halot.nikitazolin.bot.repository;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.DatabaseVendor;
import halot.nikitazolin.bot.repository.prepare.DbDataSource;
import halot.nikitazolin.bot.repository.prepare.DbH2Creator;
import halot.nikitazolin.bot.repository.prepare.FlywayMigrationRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbService {

  private final AuthorizationData authorizationData;
  private final DbDataSource dbDataSource;
  private final DbH2Creator dbH2Creator;
  private final FlywayMigrationRunner flywayMigrationRunner;

  public void validateDb(String filePath) {
    checkEnabledDb(filePath);
    prepareTempDb();
  }

  private void checkEnabledDb(String filePath) {
    if (authorizationData.getDatabase().isDbEnabled() == true) {
      if (authorizationData.getDatabase().getDbVendor() == DatabaseVendor.H2) {
        log.info("Ensure exists H2 database");
        dbH2Creator.ensureExistsDatabase(filePath);
      }

      dbDataSource.registerDataSourceСonstant();
      flywayMigrationRunner.migrateDatabaseConstant();
    }
  }

  private void prepareTempDb() {
    flywayMigrationRunner.migrateDatabaseTemporary();
  }
}
