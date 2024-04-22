package halot.nikitazolin.bot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.AudioService;
import halot.nikitazolin.bot.discord.JdaService;
import halot.nikitazolin.bot.init.authorization.AuthorizationService;
import halot.nikitazolin.bot.init.discord.DatabaseFillService;
import halot.nikitazolin.bot.init.settings.SettingsService;
import halot.nikitazolin.bot.repository.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  private final AuthorizationService authorizationService;
  private final DatabaseService databaseService;
  private final SettingsService settingsService;
  private final JdaService jdaService;
  private final AudioService audioService;
  private final DatabaseFillService databaseFillService;

  public static final String AUTHORIZATION_FILE_PATH = "secrets.yml";
  public static final String SETTINGS_FILE_PATH = "settings.yml";

  @Override
  public void run(ApplicationArguments args) throws Exception {
    authorizationService.validateAuthorization(AUTHORIZATION_FILE_PATH);
    databaseService.validateDb(AUTHORIZATION_FILE_PATH);
    settingsService.validateSettings(SETTINGS_FILE_PATH);

    // Start JDA
    // TODO Need improve guild getter. Now it potential bug
//    jdaService.initializeJda();
//
//    audioService.makeAudioPlayer(jdaService.getGuild());
//
//    databaseFillService.saveGuildToDb(jdaService.getGuild());

    System.out.println("Ready!");
    log.info("Ready!");
  }
}
