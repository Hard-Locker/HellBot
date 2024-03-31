package halot.nikitazolin.bot.repository;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Lazy
@Slf4j
@RequiredArgsConstructor
public class FlywayMigrationRunner {

  private final DataSource dataSource;

  public void migrateDatabaseConstant() {
    Flyway flyway = Flyway.configure().dataSource(dataSource).baselineOnMigrate(true).load();
    flyway.migrate();
    
    log.info("Successfully applied migration");
  }
}
