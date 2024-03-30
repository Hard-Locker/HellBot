package halot.nikitazolin.bot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.audio.AudioService;
import halot.nikitazolin.bot.audio.player.AudioPlayerListenerService;
import halot.nikitazolin.bot.audio.player.IPlayerService;
import halot.nikitazolin.bot.audio.player.TrackScheduler;
import halot.nikitazolin.bot.command.manager.CommandService;
import halot.nikitazolin.bot.init.authorization.AuthorizationConsoleMenu;
import halot.nikitazolin.bot.init.authorization.AuthorizationFileChecker;
import halot.nikitazolin.bot.init.authorization.AuthorizationLoader;
import halot.nikitazolin.bot.init.config.ConfigChecker;
import halot.nikitazolin.bot.init.config.ConfigLoader;
import halot.nikitazolin.bot.jda.JdaService;
import halot.nikitazolin.bot.listener.JdaListenerService;
import halot.nikitazolin.bot.repository.DbCreator;
import halot.nikitazolin.bot.repository.DbDataSource;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

@Service
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  private final AuthorizationFileChecker authorizationFileChecker;
  private final AuthorizationConsoleMenu authorizationConsoleMenu;
  private final AuthorizationLoader authorizationLoader;
  private final DbDataSource dbDataSource;
  private final DbCreator dbCreator;
  private final ConfigChecker configChecker;
  private final ConfigLoader configLoader;
  private final JdaService jdaService;
  private final CommandService commandService;
  private final JdaListenerService jdaListenerService;
  private final IPlayerService playerService;
  private final AudioService audioService;
  private final TrackScheduler trackScheduler;
  private final AudioPlayerListenerService audioPlayerListenerService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    authorization();
    configuration();
    dbPrepare();

//    initializeJda();
//
//    // TODO Need improve guild getter. Now it potential bug
//    makeAudioPlayer();

    System.out.println("Ready!");
  }

  private void authorization() {
    String filePath = "secrets.yml";
    boolean authorizationExists = authorizationFileChecker.ensureFileExists(filePath);

    if (authorizationExists == false) {
      authorizationConsoleMenu.showMenu(filePath);
    }

    authorizationLoader.load(filePath);
  }

  private void dbPrepare() {
    // TODO need check dbEnabled status
    dbCreator.createDatabase("secrets.yml");

    dbDataSource.registerDataSourceBean();
  }

  private void configuration() {
    String filePath = "config.yml";
    configChecker.ensureFileExists(filePath);

    configLoader.load(filePath);
  }

  private void initializeJda() {
    jdaService.makeJda();
    commandService.addCommands();
    jdaListenerService.addListeners();
  }

  private void makeAudioPlayer() {
    // TODO Need improve guild getter. Now it potential bug
    Guild guild = jdaService.getJda().get().getGuilds().getFirst();
    playerService.createPlayer();
    trackScheduler.preparateScheduler(playerService);
    audioPlayerListenerService.addListeners();
    audioService.registratePlayer(guild);
  }
}
