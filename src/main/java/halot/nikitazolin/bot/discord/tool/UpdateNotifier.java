package halot.nikitazolin.bot.discord.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.util.VersionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateNotifier {

  private final Settings settings;
  private final VersionChecker versionChecker;
  private final MessageSender messageSender;
  private final MessageFormatter messageFormatter;
  private final DiscordDataReceiver discordDataReceiver;

  public void checkUpdate() {
    if (settings.isUpdateNotification() == false) {
      return;
    }

    String latestVersion = versionChecker.getNumberLatestVersion().orElse("unknown");
    String currentVersion = versionChecker.getNumberCurrentVersion();
    Set<Long> userIds = new HashSet<>();

    if (latestVersion.equals(currentVersion)) {
      log.debug("The current version {} is up to date.", currentVersion);
      return;
    }

    log.info("New version available: {}", latestVersion);

    if (settings.getOwnerUserId() != null) {
      userIds.add(settings.getOwnerUserId());
    }

    if (settings.getAdminUserIds() != null) {
      userIds.addAll(settings.getAdminUserIds());
    }

    List<User> users = discordDataReceiver.getUsersByIds(new ArrayList<>(userIds));
    EmbedBuilder embed = messageFormatter.createInfoEmbed("Available latest update: " + latestVersion);

    for (User user : users) {
      messageSender.sendPrivateMessage(user, embed);
      log.info("Update notification sent to user: {}", user);
    }
  }
}
