package halot.nikitazolin.bot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.audio.AudioService;
import halot.nikitazolin.bot.audio.player.AudioPlayerListenerService;
import halot.nikitazolin.bot.audio.player.IPlayerService;
import halot.nikitazolin.bot.audio.player.TrackScheduler;
import halot.nikitazolin.bot.command.manager.CommandService;
import halot.nikitazolin.bot.init.ConfigChecker;
import halot.nikitazolin.bot.init.SecretChecker;
import halot.nikitazolin.bot.jda.JdaService;
import halot.nikitazolin.bot.listener.JdaListenerService;
import halot.nikitazolin.bot.util.ConfigLoader;
import halot.nikitazolin.bot.view.ConsoleMenu;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

@Component
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  private final SecretChecker secretChecker;
  private final ConfigChecker configChecker;
  private final ConfigLoader configLoader;
  private final ConsoleMenu consoleMenu;
  private final JdaService jdaService;
  private final CommandService commandService;
  private final JdaListenerService jdaListenerService;
  private final IPlayerService playerService;
  private final AudioService audioService;
  private final TrackScheduler trackScheduler;
  private final AudioPlayerListenerService audioPlayerListenerService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    checkAuthorization();
    checkConfiguration();
    
    //Make base application
    jdaService.makeJda();
    commandService.addCommands();
    jdaListenerService.addListeners();
    
    //Make audio player complete instance
    //TODO Need improve guild getter. Now it potential bug
    Guild guild = jdaService.getJda().get().getGuilds().getFirst();
    playerService.createPlayer();
    trackScheduler.preparateScheduler(playerService);
    audioPlayerListenerService.addListeners();
    audioService.registratePlayer(guild);
  }
  
  private void checkAuthorization() {
    boolean secretExists = secretChecker.ensureSecretExists("secrets.yml");
    
    if (secretExists == false) {
      //TODO formating save file
      consoleMenu.showMenu("secrets.yml");
    }
    //TODO loader
//  configLoader.loadConfig("secrets.yml");
  }
  
  private void checkConfiguration() {
    configChecker.ensureConfigExists("config.yml");
//  configLoader.loadConfig("config.yml");
  }
}
