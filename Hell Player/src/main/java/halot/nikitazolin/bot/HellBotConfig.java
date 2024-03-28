package halot.nikitazolin.bot;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.Profile;

import halot.nikitazolin.bot.init.authorization.data.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.data.Database;
import lombok.RequiredArgsConstructor;

@Configuration
@ComponentScan(basePackages = "halot.nikitazolin.bot")
@RequiredArgsConstructor
public class HellBotConfig {

  private final AuthorizationData authorizationData;

  @Bean
  @Profile("db")
  DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    Database dbConfig = authorizationData.getDatabase();

    dataSource.setDriverClassName(dbConfig.getDbVendor().getDriverClassName());
    dataSource.setUrl(dbConfig.getDbUrl());
    dataSource.setUsername(dbConfig.getDbUsername());
    dataSource.setPassword(dbConfig.getDbPassword());

    return dataSource;
  }
}
