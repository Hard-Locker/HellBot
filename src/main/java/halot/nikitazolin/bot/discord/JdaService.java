package halot.nikitazolin.bot.discord;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.discord.listener.JdaListenerService;
import halot.nikitazolin.bot.discord.tool.StatusManager;
import halot.nikitazolin.bot.discord.tool.UpdateNotifier;
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
  private final StatusManager statusManager;
  private final UpdateNotifier updateNotifier;

  private Guild guild;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public void initializeJda() {
    jdaMaker.makeJda();
    commandRegistrationService.addCommands();
    jdaListenerService.addListeners();

    writeGuild();
    configureAfterStartup();
    startUpdateCheckScheduler();

    log.info("Initialize JDA");
  }

  private void configureAfterStartup() {
    jdaMaker.getJda().ifPresent(jda -> statusManager.loadStatusFromSettings());
  }

  private void writeGuild() {
    jdaMaker.getJda().ifPresent(jda -> {
      Guild firstGuild = jda.getGuilds().getFirst();
      guild = firstGuild;
    });
  }

  private void startUpdateCheckScheduler() {
    scheduler.scheduleAtFixedRate(() -> {
      try {
        log.debug("Checking for updates...");
        updateNotifier.checkUpdate();
      } catch (Exception e) {
        log.warn("Error occurred while checking for updates", e);
      }
    }, 0, 1, TimeUnit.DAYS);
  }
}
