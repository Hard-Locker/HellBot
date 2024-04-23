package halot.nikitazolin.bot.discord;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.discord.listener.JdaListenerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class JdaService {

  private final JdaMaker jdaMaker;
  private final CommandRegistrationService commandRegistrationService;
  private final JdaListenerService jdaListenerService;
  
  private Guild guild;

  public void initializeJda() {
    jdaMaker.makeJda();
    commandRegistrationService.addCommands();
    jdaListenerService.addListeners();
    
    writeGuild();

    log.info("Initialize JDA");
  }
  
  private void writeGuild() {
    guild = jdaMaker.getJda().get().getGuilds().getFirst();
  }
}
