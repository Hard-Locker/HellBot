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
//    Yaml yaml = new Yaml(new Constructor(Config.class, new LoaderOptions()));
//    
//    try (InputStream inputStream = new FileInputStream(filePath)) {
//      log.info("File loaded successfully from {}", filePath);
//      Config loadedConfig = yaml.load(inputStream);
//      
//      if (loadedConfig != null) {
//        config.setVolume(loadedConfig.getVolume());
//        config.setOwnerIds(loadedConfig.getOwnerIds());
//        config.setAloneTimeUntilStop(loadedConfig.getAloneTimeUntilStop());
//        config.setBotStatusAtStart(loadedConfig.getBotStatusAtStart());
//        config.setBotActivityAtStart(loadedConfig.getBotActivityAtStart());
//        config.setSongInStatus(loadedConfig.isSongInStatus());
//        config.setStayInChannel(loadedConfig.isStayInChannel());
//        config.setUpdateAlerts(loadedConfig.isUpdateAlerts());
//        config.setAllowedTextChannelIds(loadedConfig.getAllowedTextChannelIds());
//        config.setAllowedVoiceChannelIds(loadedConfig.getAllowedVoiceChannelIds());
//        config.setDjUserIds(loadedConfig.getDjUserIds());
//        config.setBannedUserIds(loadedConfig.getBannedUserIds());
//        config.setPlaylistFolderPaths(loadedConfig.getPlaylistFolderPaths());
//        config.setPrefixes(loadedConfig.getPrefixes());
//        config.setNameAliases(loadedConfig.getNameAliases());
//      }
//    } catch (IOException e) {
//      log.error("Error reading config file: {}", e.getMessage());
//    }
//    
//    config.printFieldValues();
  }
}
