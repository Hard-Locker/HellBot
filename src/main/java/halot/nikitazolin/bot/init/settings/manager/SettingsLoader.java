package halot.nikitazolin.bot.init.settings.manager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettingsLoader {

  private final Settings settings;

  public void load(String filePath) {
    Yaml yaml = new Yaml(new Constructor(Settings.class, new LoaderOptions()));

    try (InputStream inputStream = new FileInputStream(filePath)) {
      log.info("Loading configuration file from {}", filePath);
      Settings loadedConfig = yaml.load(inputStream);

      if (loadedConfig != null) {
        safelyAssignSettings(loadedConfig);
      }
    } catch (Exception e) {
      log.error("Failed to load configuration from file: {}", e.getMessage());
    }
  }

  private void safelyAssignSettings(Settings loadedConfig) {
    try {
      settings.setLanguage(defaultIfNull(loadedConfig.getLanguage(), "en"));
      settings.setVolume(defaultIfNull(loadedConfig.getVolume(), 100));
      settings.setOwnerUserId(defaultIfNull(loadedConfig.getOwnerUserId(), 0L));
      settings.setAloneTimeUntilStop(defaultIfNull(loadedConfig.getAloneTimeUntilStop(), 120));
      settings.setBotStatus(defaultIfNull(loadedConfig.getBotStatus(), "online"));
      settings.setSongInStatus(defaultIfNull(loadedConfig.isSongInStatus(), true));
      settings.setSongInTopic(defaultIfNull(loadedConfig.isSongInTopic(), false));
      settings.setSongInTextChannel(defaultIfNull(loadedConfig.isSongInTextChannel(), false));
      settings.setStayInChannel(defaultIfNull(loadedConfig.isStayInChannel(), true));
      settings.setUpdateAlerts(defaultIfNull(loadedConfig.isUpdateAlerts(), true));
      settings.setAllowedTextChannelIds(defaultIfNull(loadedConfig.getAllowedTextChannelIds(), new ArrayList<>()));
      settings.setAllowedVoiceChannelIds(defaultIfNull(loadedConfig.getAllowedVoiceChannelIds(), new ArrayList<>()));
      settings.setAdminUserIds(defaultIfNull(loadedConfig.getAdminUserIds(), new ArrayList<>()));
      settings.setDjUserIds(defaultIfNull(loadedConfig.getDjUserIds(), new ArrayList<>()));
      settings.setBannedUserIds(defaultIfNull(loadedConfig.getBannedUserIds(), new ArrayList<>()));
      settings.setPlaylistFolderPaths(defaultIfNull(loadedConfig.getPlaylistFolderPaths(), new ArrayList<>()));
      settings.setPrefixes(defaultIfNull(loadedConfig.getPrefixes(), new ArrayList<>()));
      settings.setNameAliases(defaultIfNull(loadedConfig.getNameAliases(), new HashMap<>()));
      log.debug("Successfully applied settings");
    } catch (Exception e) {
      log.error("Error applying settings: {}", e.getMessage());
    }
  }

  private <T> T defaultIfNull(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
