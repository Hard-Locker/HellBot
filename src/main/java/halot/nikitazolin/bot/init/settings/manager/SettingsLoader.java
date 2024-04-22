package halot.nikitazolin.bot.init.settings.manager;

import java.io.FileInputStream;
import java.io.IOException;
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
      log.info("File loaded successfully from {}", filePath);
      Settings loadedConfig = yaml.load(inputStream);

      if (loadedConfig != null) {
        settings.setVolume(loadedConfig.getVolume());
        settings.setOwnerIds(loadedConfig.getOwnerIds());
        settings.setAloneTimeUntilStop(loadedConfig.getAloneTimeUntilStop());
        settings.setBotStatusAtStart(loadedConfig.getBotStatusAtStart());
        settings.setBotActivityAtStart(loadedConfig.getBotActivityAtStart());
        settings.setSongInStatus(loadedConfig.isSongInStatus());
        settings.setStayInChannel(loadedConfig.isStayInChannel());
        settings.setUpdateAlerts(loadedConfig.isUpdateAlerts());
        settings.setAllowedTextChannelIds(loadedConfig.getAllowedTextChannelIds());
        settings.setAllowedVoiceChannelIds(loadedConfig.getAllowedVoiceChannelIds());
        settings.setDjUserIds(loadedConfig.getDjUserIds());
        settings.setBannedUserIds(loadedConfig.getBannedUserIds());
        settings.setPlaylistFolderPaths(loadedConfig.getPlaylistFolderPaths());
        settings.setPrefixes(loadedConfig.getPrefixes());
        settings.setNameAliases(loadedConfig.getNameAliases());
      }
    } catch (IOException e) {
      log.error("Error reading config file: {}", e.getMessage());
    }
  }
}
