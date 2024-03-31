package halot.nikitazolin.bot.init.config;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.config.manager.ConfigChecker;
import halot.nikitazolin.bot.init.config.manager.ConfigLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigService {

  private final ConfigChecker configChecker;
  private final ConfigLoader configLoader;

  public void validateConfiguration() {
    String filePath = "config.yml";
    configChecker.ensureFileExists(filePath);

    configLoader.load(filePath);

    log.info("Load config");
  }
}
