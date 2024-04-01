package halot.nikitazolin.bot.repository.prepare;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.Database;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSourceRegistrator {

  private final ApplicationContext applicationContext;
  private final AuthorizationData authorizationData;

  public void registerDataSourceСonstant() {
    Database database = authorizationData.getDatabase();

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(database.getDbVendor().getDriverClassName());
    dataSource.setUrl(database.getDbUrl());
    dataSource.setUsername(database.getDbUsername());
    dataSource.setPassword(database.getDbPassword());

    ((GenericApplicationContext) applicationContext).registerBean("dataSourceСonstant", DataSource.class,
        () -> dataSource);

    log.info("Successfully register bean DataSource for constant database");
  }
}
