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
import halot.nikitazolin.bot.init.authorization.AuthorizationConsoleMenu;
import halot.nikitazolin.bot.init.authorization.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.AuthorizationFileChecker;
import halot.nikitazolin.bot.init.authorization.AuthorizationLoader;
import halot.nikitazolin.bot.init.config.ConfigChecker;
import halot.nikitazolin.bot.jda.JdaService;
import halot.nikitazolin.bot.listener.JdaListenerService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

@Component
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  private final AuthorizationData authorizationData;
  private final AuthorizationFileChecker authorizationFileChecker;
  private final AuthorizationConsoleMenu authorizationConsoleMenu;
  private final AuthorizationLoader authorizationLoader;
  private final ConfigChecker configChecker;
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
  
  private void authorization() {
    String filePath = "secrets.yml";
    boolean authorizationExists = authorizationFileChecker.ensureFileExists(filePath);

    if (authorizationExists == false) {
      authorizationConsoleMenu.showMenu(filePath);
    }
    
    System.out.println("Before loading");
    System.out.println("ApiKey: " + authorizationData.getDiscordApi().getApiKey());
    System.out.println("YoutubeEnabled: " + authorizationData.getYoutube().isYoutubeEnabled());
    System.out.println("YoutubeLogin: " + authorizationData.getYoutube().getYoutubeLogin());
    System.out.println("YoutubePassword: " + authorizationData.getYoutube().getYoutubePassword());
    System.out.println("DbEnabled: " + authorizationData.getDatabase().isDbEnabled());
    System.out.println("DbName: " + authorizationData.getDatabase().getDbName());
    System.out.println("DbUrl: " + authorizationData.getDatabase().getDbUrl());
    System.out.println("DbUsername: " + authorizationData.getDatabase().getDbUsername());
    System.out.println("DbPassword: " + authorizationData.getDatabase().getDbPassword());
    
    authorizationLoader.load(filePath);
    
    System.out.println("After loading");
    System.out.println("ApiKey: " + authorizationData.getDiscordApi().getApiKey());
    System.out.println("YoutubeEnabled: " + authorizationData.getYoutube().isYoutubeEnabled());
    System.out.println("YoutubeLogin: " + authorizationData.getYoutube().getYoutubeLogin());
    System.out.println("YoutubePassword: " + authorizationData.getYoutube().getYoutubePassword());
    System.out.println("DbEnabled: " + authorizationData.getDatabase().isDbEnabled());
    System.out.println("DbName: " + authorizationData.getDatabase().getDbName());
    System.out.println("DbUrl: " + authorizationData.getDatabase().getDbUrl());
    System.out.println("DbUsername: " + authorizationData.getDatabase().getDbUsername());
    System.out.println("DbPassword: " + authorizationData.getDatabase().getDbPassword());
  }
  
  private void configuration() {
    String filePath = "config.yml";
    configChecker.ensureConfigExists(filePath);
//  yamlLoader.loadConfig(filePath);
  }
}
