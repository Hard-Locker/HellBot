package halot.nikitazolin.bot.repository;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class FlywayMigrationRunner {

  private final ApplicationContext applicationContext;

  public void migrateDatabaseTemporary() {
    DataSource dataSourceTemporary = (DataSource) applicationContext.getBean("dataSourceTemporary");
    Flyway flywayTemporary = Flyway.configure().dataSource(dataSourceTemporary).baselineOnMigrate(true).load();

    flywayTemporary.migrate();
    log.info("Successfully applied migration for temporary database");
  }

  public void migrateDatabaseConstant() {
    DataSource dataSourceConstant = (DataSource) applicationContext.getBean("dataSourceСonstant");
    Flyway flywayConstant = Flyway.configure().dataSource(dataSourceConstant).baselineOnMigrate(true).load();

    flywayConstant.migrate();
    log.info("Successfully applied migration for constant database");
  }
}
