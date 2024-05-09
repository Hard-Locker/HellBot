package halot.nikitazolin.bot.init.settings.manager;

import java.io.FileInputStream;
import java.io.InputStream;

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
      settings.setVolume(defaultIfNull(loadedConfig.getVolume(), 0));
      settings.setOwnerUserId(defaultIfNull(loadedConfig.getOwnerUserId(), 0L));
      settings.setAloneTimeUntilStop(defaultIfNull(loadedConfig.getAloneTimeUntilStop(), 0L));
      settings.setBotStatus(loadedConfig.getBotStatus());
      settings.setBotActivity(loadedConfig.getBotActivity());
      settings.setSongInStatus(loadedConfig.isSongInStatus());
      settings.setStayInChannel(loadedConfig.isStayInChannel());
      settings.setUpdateAlerts(loadedConfig.isUpdateAlerts());
      settings.setAllowedTextChannelIds(loadedConfig.getAllowedTextChannelIds());
      settings.setAllowedVoiceChannelIds(loadedConfig.getAllowedVoiceChannelIds());
      settings.setAdminUserIds(loadedConfig.getAdminUserIds());
      settings.setDjUserIds(loadedConfig.getDjUserIds());
      settings.setBannedUserIds(loadedConfig.getBannedUserIds());
      settings.setPlaylistFolderPaths(loadedConfig.getPlaylistFolderPaths());
      settings.setPrefixes(loadedConfig.getPrefixes());
      settings.setNameAliases(loadedConfig.getNameAliases());
    } catch (Exception e) {
      log.error("Error applying settings: {}", e.getMessage());
    }
  }

  private <T> T defaultIfNull(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
