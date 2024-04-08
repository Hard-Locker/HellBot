package halot.nikitazolin.bot.repository.prepare;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.HellBotConfig;
import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.Database;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DbRegistrator {

  private final HellBotConfig hellBotConfig;
  private final AuthorizationData authorizationData;

  public void registerConstantDb() {
    Database database = authorizationData.getDatabase();

    String tenantId = "constant";
    String driverClassName = database.getDbVendor().getDriverClassName();
    String url = database.getDbUrl();
    String username = database.getDbUsername();
    String password = database.getDbPassword();

    try {
      hellBotConfig.addTenant(tenantId, driverClassName, url, username, password);
      hellBotConfig.setCurrentTenant(tenantId);
      log.info("Add constant database");
    } catch (Exception e) {
      log.info("Error with adding constant database: " + e);
    }
  }
}
