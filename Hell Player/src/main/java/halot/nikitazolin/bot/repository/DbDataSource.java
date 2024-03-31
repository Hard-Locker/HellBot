package halot.nikitazolin.bot.repository;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.init.authorization.data.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.data.Database;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DbDataSource {

  private final ApplicationContext applicationContext;
  private final AuthorizationData authorizationData;

  public void registerDataSourceСonstant() {
    Database dbConfig = authorizationData.getDatabase();

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(dbConfig.getDbVendor().getDriverClassName());
    dataSource.setUrl(dbConfig.getDbUrl());
    dataSource.setUsername(dbConfig.getDbUsername());
    dataSource.setPassword(dbConfig.getDbPassword());

    ((GenericApplicationContext) applicationContext).registerBean("dataSourceСonstant", DataSource.class, () -> dataSource);
  }
}
