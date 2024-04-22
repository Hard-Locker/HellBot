package halot.nikitazolin.bot.init.settings;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.settings.manager.SettingsFileChecker;
import halot.nikitazolin.bot.init.settings.manager.SettingsLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SettingsService {

  private final SettingsFileChecker settingsFileChecker;
  private final SettingsLoader settingsLoader;

  public void validateSettings(String filePath) {
    settingsFileChecker.ensureFileExists(filePath);

    settingsLoader.load(filePath);

    log.info("Loaded {}", filePath);
  }
}
