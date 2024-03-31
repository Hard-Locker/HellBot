package halot.nikitazolin.bot.repository;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.authorization.data.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.data.DatabaseVendor;
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
    checkDbSchema();
  }

  private void checkEnabledDb(String filePath) {
    if (authorizationData.getDatabase().isDbEnabled() == true) {
      if (authorizationData.getDatabase().getDbVendor() == DatabaseVendor.H2) {
        log.info("Ensure exists database");
        dbH2Creator.ensureExistsDatabase(filePath);
      }

      dbDataSource.registerDataSourceСonstant();
    }
  }

  private void checkDbSchema() {
    flywayMigrationRunner.migrateDatabaseConstant();
  }
}
