package halot.nikitazolin.bot;

//import javax.sql.DataSource;

//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan(basePackages = "halot.nikitazolin.bot")
public class HellBotConfig {
  
//  @Bean
//  DataSource dataSource() {
//    DriverManagerDataSource dataSource = new DriverManagerDataSource();
//    dataSource.setDriverClassName("org.postgresql.Driver");
//    dataSource.setUrl("jdbc:h2:mem:testdb;MODE=PostgreSQL");
//    dataSource.setUsername("odmen");
//    dataSource.setPassword("admin");
//    return dataSource;
//  }
}
