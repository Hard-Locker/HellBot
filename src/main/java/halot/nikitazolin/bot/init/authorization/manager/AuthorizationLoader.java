package halot.nikitazolin.bot.init.authorization.manager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.Database;
import halot.nikitazolin.bot.init.authorization.model.DatabaseVendor;
import halot.nikitazolin.bot.init.authorization.model.DiscordApi;
import halot.nikitazolin.bot.init.authorization.model.Youtube;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationLoader {

  private final AuthorizationData authorizationData;

  public void load(String filePath) {
    Map<String, Object> fileData = loadYamlConfiguration(filePath);

    loadDiscordApi(fileData);
    loadYoutube(fileData);
    loadDatabase(fileData);
  }

  private Map<String, Object> loadYamlConfiguration(String filePath) {
    Yaml yaml = new Yaml();

    try (InputStream inputStream = new FileInputStream(filePath)) {
      log.info("File loaded successfully from {}", filePath);

      return yaml.load(inputStream);
    } catch (Exception e) {
      log.error("Unable to load file from {}: {}", filePath, e.getMessage());

      return Collections.emptyMap();
    }
  }

  private void loadDiscordApi(Map<String, Object> config) {
    Map<String, Object> discordApiConfig = getMap(config, "discordApi");

    if (discordApiConfig != null) {
      DiscordApi discordApi = new DiscordApi();
      discordApi.setApiKey(getString(discordApiConfig, "apiKey"));

      authorizationData.setDiscordApi(discordApi);
      log.info("Authorization data (DiscordApi) loaded successfully");
    }
  }

  private void loadYoutube(Map<String, Object> config) {
    Map<String, Object> youtubeConfig = getMap(config, "youtube");

    if (youtubeConfig != null) {
      Youtube youtube = new Youtube();
      youtube.setYoutubeEnabled(getBoolean(youtubeConfig, "youtubeEnabled"));
      youtube.setYoutubeLogin(getString(youtubeConfig, "youtubeLogin"));
      youtube.setYoutubePassword(getString(youtubeConfig, "youtubePassword"));
      youtube.setYoutubeAccessToken(getString(youtubeConfig, "youtubeAccessToken"));

      authorizationData.setYoutube(youtube);
      log.info("Authorization data (Youtube) loaded successfully");
    }
  }

  private void loadDatabase(Map<String, Object> config) {
    Map<String, Object> dbConfig = getMap(config, "database");

    if (dbConfig != null) {
      String dbVendorName = getString(dbConfig, "dbVendor");

      DatabaseVendor.fromString(dbVendorName).ifPresentOrElse(dbVendor -> {
        Database database = new Database();
        database.setDbEnabled(getBoolean(dbConfig, "dbEnabled"));
        database.setDbVendor(dbVendor);
        database.setDbName(getString(dbConfig, "dbName"));
        database.setDbUrl(getString(dbConfig, "dbUrl"));
        database.setDbUsername(getString(dbConfig, "dbUsername"));
        database.setDbPassword(getString(dbConfig, "dbPassword"));

        authorizationData.setDatabase(database);
        log.info("Authorization data (Database) loaded successfully");
      }, () -> {
        log.error("Unsupported database vendor: {}", dbVendorName);
      });
    }
  }

  private String getString(Map<String, Object> config, String key) {
    Object value = config.get(key);
    return value instanceof String ? (String) value : null;
  }

  private Boolean getBoolean(Map<String, Object> config, String key) {
    Object value = config.get(key);
    return value instanceof Boolean ? (Boolean) value : false;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getMap(Map<String, Object> config, String key) {
    Object value = config.get(key);
    return value instanceof Map ? (Map<String, Object>) value : null;
  }
}
