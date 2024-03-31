package halot.nikitazolin.bot;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import lombok.RequiredArgsConstructor;

@Configuration
@ComponentScan(basePackages = "halot.nikitazolin.bot")
@RequiredArgsConstructor
public class HellBotConfig {

  @Bean
  DataSource dataSourceTemporary() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:TemporaryDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    dataSource.setUsername("sa");
    dataSource.setPassword("admin");

    return dataSource;
  }
}
