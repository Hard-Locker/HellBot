package halot.nikitazolin.bot.discord;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.command.manager.CommandService;
import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.discord.listener.JdaListenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JdaService {

  private final JdaMaker jdaMaker;
  private final CommandService commandService;
  private final JdaListenerService jdaListenerService;

  public void initializeJda() {
    jdaMaker.makeJda();
    commandService.addCommands();
    jdaListenerService.addListeners();

    log.info("Initialize JDA");
  }
}
