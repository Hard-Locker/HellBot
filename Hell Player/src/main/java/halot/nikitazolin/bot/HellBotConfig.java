package halot.nikitazolin.bot;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@ComponentScan(basePackages = "halot.nikitazolin.bot")
@RequiredArgsConstructor
public class HellBotConfig {

  private final ThreadLocal<String> currentTenant = new ThreadLocal<>();
  private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();

  private AbstractRoutingDataSource multiTenantDataSource;

  @Bean
  DataSource dataSource() {
    multiTenantDataSource = new AbstractRoutingDataSource() {
      @Override
      protected Object determineCurrentLookupKey() {
        return currentTenant.get();
      }
    };

    multiTenantDataSource.setTargetDataSources(tenantDataSources);
    multiTenantDataSource.setDefaultTargetDataSource(defaultDataSource());
    multiTenantDataSource.afterPropertiesSet();

    return multiTenantDataSource;
  }

  public void addTenant(String tenantId, String driverClassName, String url, String username, String password)
      throws SQLException {
    DataSource dataSource = DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username)
        .password(password).build();

    try (Connection connection = dataSource.getConnection()) {
      tenantDataSources.put(tenantId, dataSource);
      multiTenantDataSource.afterPropertiesSet();
    } catch (SQLException e) {
      log.error("Error with add new tenant: " + e);
    }
  }

  public void setCurrentTenant(String tenantId) {
    currentTenant.set(tenantId);
  }

  private DriverManagerDataSource defaultDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:TemporaryDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    dataSource.setUsername("sa");
    dataSource.setPassword("admin");

    return dataSource;
  }

//  @Bean
//  DataSource dataSourceTemporary() {
//    DriverManagerDataSource dataSource = new DriverManagerDataSource();
//
//    dataSource.setDriverClassName("org.h2.Driver");
//    dataSource.setUrl("jdbc:h2:file:./db/HellDB;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE");
//    dataSource.setUsername("ODMEN");
//    dataSource.setPassword("ADMIN");
//
//    return dataSource;
//  }

//  @Bean
//  DataSource dataSourceConstant() {
//    DriverManagerDataSource dataSource = new DriverManagerDataSource();
//
//    dataSource.setDriverClassName("org.postgresql.Driver");
//    dataSource.setUrl("jdbc:postgresql://localhost:5432/helldb");
//    dataSource.setUsername("odmen");
//    dataSource.setPassword("admin");
//
//    return dataSource;
//  }
}
