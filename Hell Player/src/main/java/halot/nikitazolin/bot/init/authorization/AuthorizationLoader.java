package halot.nikitazolin.bot.init.authorization;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.init.authorization.data.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.data.Database;
import halot.nikitazolin.bot.init.authorization.data.DatabaseVendor;
import halot.nikitazolin.bot.init.authorization.data.DiscordApi;
import halot.nikitazolin.bot.init.authorization.data.Youtube;
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
    @SuppressWarnings("unchecked")
    Map<String, Object> discordApiConfig = (Map<String, Object>) config.get("discordApi");

    if (discordApiConfig != null) {
      DiscordApi discordApi = new DiscordApi();
      discordApi.setApiKey((String) discordApiConfig.get("apiKey"));

      authorizationData.setDiscordApi(discordApi);
      log.info("Authorization data (DiscordApi) loaded successfully");
    }
  }

  private void loadYoutube(Map<String, Object> config) {
    @SuppressWarnings("unchecked")
    Map<String, Object> youtubeConfig = (Map<String, Object>) config.get("youtube");

    if (youtubeConfig != null) {
      Youtube youtube = new Youtube();
      youtube.setYoutubeEnabled((Boolean) youtubeConfig.get("youtubeEnabled"));
      youtube.setYoutubeLogin((String) youtubeConfig.get("youtubeLogin"));
      youtube.setYoutubePassword((String) youtubeConfig.get("youtubePassword"));

      authorizationData.setYoutube(youtube);
      log.info("Authorization data (Youtube) loaded successfully");
    }
  }

  private void loadDatabase(Map<String, Object> config) {
    @SuppressWarnings("unchecked")
    Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");

    if (dbConfig != null) {
      String dbVendorName = (String) dbConfig.get("dbVendor");

      DatabaseVendor.fromString(dbVendorName).ifPresentOrElse(dbVendor -> {
        Database database = new Database();
        database.setDbEnabled((Boolean) dbConfig.get("dbEnabled"));
        database.setDbVendor(dbVendor);
        database.setDbName((String) dbConfig.get("dbName"));
        database.setDbUrl((String) dbConfig.get("dbUrl"));
        database.setDbUsername((String) dbConfig.get("dbUsername"));
        database.setDbPassword((String) dbConfig.get("dbPassword"));

        authorizationData.setDatabase(database);
        log.info("Authorization data (Database) loaded successfully");
      }, () -> {
        log.error("Unsupported database vendor: {}", dbVendorName);
      });
    }
  }
}
