package halot.nikitazolin.bot.init.authorization;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import halot.nikitazolin.bot.init.authorization.data.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.data.Database;
import halot.nikitazolin.bot.init.authorization.data.DatabaseVendor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationLoader {

  private final AuthorizationData authorizationData;

  public void load(String filePath) {
    loadDiscordApi(filePath);
    loadYoutube(filePath);
    loadDatabase(filePath);
  }

  private void loadDiscordApi(String filePath) {
    TypeDescription authDataTypeDesc = new TypeDescription(AuthorizationData.class);
    Constructor constructor = new Constructor(AuthorizationData.class, new LoaderOptions());
    constructor.addTypeDescription(authDataTypeDesc);
    Yaml yaml = new Yaml(constructor);

    try (InputStream inputStream = new FileInputStream(filePath)) {
      AuthorizationData loadedData = yaml.load(inputStream);

      authorizationData.setDiscordApi(loadedData.getDiscordApi());

      log.info("Authorization data (DiscordApi) loaded successfully from {}", filePath);
    } catch (Exception e) {
      log.error("Unable to load authorization data from {}: {}", filePath, e.getMessage());
    }
  }

  private void loadYoutube(String filePath) {
    TypeDescription authDataTypeDesc = new TypeDescription(AuthorizationData.class);
    Constructor constructor = new Constructor(AuthorizationData.class, new LoaderOptions());
    constructor.addTypeDescription(authDataTypeDesc);
    Yaml yaml = new Yaml(constructor);

    try (InputStream inputStream = new FileInputStream(filePath)) {
      AuthorizationData loadedData = yaml.load(inputStream);

      authorizationData.setYoutube(loadedData.getYoutube());

      log.info("Authorization data (Youtube) loaded successfully from {}", filePath);
    } catch (Exception e) {
      log.error("Unable to load authorization data from {}: {}", filePath, e.getMessage());
    }
  }

  private void loadDatabase(String filePath) {
    Yaml yaml = new Yaml();

    try (InputStream inputStream = new FileInputStream(filePath)) {
      Map<String, Object> loadedDbData = yaml.load(inputStream);
      @SuppressWarnings("unchecked")
      Map<String, Object> dbData = (Map<String, Object>) loadedDbData.get("database");

      if (dbData != null) {
        String dbVendorName = (String) dbData.get("dbVendor");
        
        DatabaseVendor.fromString(dbVendorName).ifPresentOrElse(dbVendor -> {
          Database database = new Database();
          database.setDbEnabled((Boolean) dbData.get("dbEnabled"));
          database.setDbVendor(dbVendor);
          database.setDbName((String) dbData.get("dbName"));
          database.setDbUrl((String) dbData.get("dbUrl"));
          database.setDbUsername((String) dbData.get("dbUsername"));
          database.setDbPassword((String) dbData.get("dbPassword"));

          authorizationData.setDatabase(database);
        }, () -> {
          log.error("Unsupported database vendor: {}", dbVendorName);
        });
      }

      log.info("Authorization data (Database) loaded successfully from {}", filePath);
    } catch (Exception e) {
      log.error("Unable to load authorization data from {}: {}", filePath, e.getMessage());
    }
  }
}
