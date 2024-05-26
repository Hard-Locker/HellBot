package halot.nikitazolin.bot.discord.tool;

import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.ApplicationRunnerImpl;
import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusManager {

  private final JdaMaker jdaMaker;
  private final Settings settings;
  private final SettingsSaver settingsSaver;

  public void loadStatusFromSettings() {
    if (settings.getBotStatus() != null) {
      String status = settings.getBotStatus().toLowerCase();
      OnlineStatus onlineStatus = parseStatus(status);

      if (onlineStatus != null) {
        setStatus(onlineStatus);
      } else {
        log.debug("Failed to parse status from settings");
      }
    }
  }

  public void setOnline() {
    setStatus(OnlineStatus.ONLINE);
  }

  public void setIdle() {
    setStatus(OnlineStatus.IDLE);
  }

  public void setDnd() {
    setStatus(OnlineStatus.DO_NOT_DISTURB);
  }

  public void setInvisible() {
    setStatus(OnlineStatus.INVISIBLE);
  }

  public void setOffline() {
    setStatus(OnlineStatus.OFFLINE);
  }

  private void setStatus(OnlineStatus status) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();
      jda.getPresence().setStatus(status);

      settings.setBotStatus(status.name().toLowerCase());
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
      log.debug("Online status set to: " + status);
    } else {
      log.error("Failed to set activity: JDA instance not available");
    }
  }

  private OnlineStatus parseStatus(String status) {
    switch (status) {
    case "online":
      return OnlineStatus.ONLINE;

    case "idle":
      return OnlineStatus.IDLE;

    case "dnd", "do_not_disturb":
      return OnlineStatus.DO_NOT_DISTURB;

    case "invisible":
      return OnlineStatus.INVISIBLE;

    case "offline":
      return OnlineStatus.OFFLINE;

    default:
      return null;
    }
  }
}
