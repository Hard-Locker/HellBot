package halot.nikitazolin.bot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.audio.BotAudioService;
import halot.nikitazolin.bot.audio.IPlayerManager;
import halot.nikitazolin.bot.command.manager.CommandAdder;
import halot.nikitazolin.bot.jda.JdaService;
import halot.nikitazolin.bot.listener.ListnerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

@Component
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  private final JdaService jdaService;
  private final CommandAdder commandRegistrator;
  private final ListnerManager listnerManager;
  private final IPlayerManager botPlayerManager;
  private final BotAudioService botAudioService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    //Make base application
    jdaService.makeJda();
    commandRegistrator.addCommands();
    listnerManager.addListners();
    
    //Make audio player complete instance
    //TODO Need improve guild getter. Now it potential bug
    Guild guild = jdaService.getJda().get().getGuilds().getFirst();
    botPlayerManager.createPlayer();
    botAudioService.registratePlayer(guild);
  }
}
