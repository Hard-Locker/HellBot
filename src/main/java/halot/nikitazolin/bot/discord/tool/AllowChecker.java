package halot.nikitazolin.bot.discord.tool;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class AllowChecker {

  private final MessageFormatter messageFormatter;
  private final Settings settings;
  
  public boolean checkAllowedTextChannel(TextChannel textChannel, User user) {
    List<Long> allowedTextChannelIds = new ArrayList<>();

    try {
      allowedTextChannelIds.addAll(settings.getAllowedTextChannelIds());
    } catch (NullPointerException e) {
      log.debug("Especial allowed text channel not set. Bot can connect read any channel.");

      return true;
    }

    if (allowedTextChannelIds.isEmpty() || allowedTextChannelIds.contains(textChannel.getIdLong())) {
      return true;
    } else {
      EmbedBuilder embed = messageFormatter.createErrorEmbed(textChannel.getName() + " text channel is denied for bot.");
      MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embed.build()).build();

      if (!user.isBot()) {
        user.openPrivateChannel().queue((privateChannel) -> {
          privateChannel.sendMessage(messageCreateData).queue();
        }, (error) -> {
          log.warn("Failed to send a private message to the user: " + user);
        });
      }

      log.debug("User tried to use a command in an denied text channel.");

      return false;
    }
  }
}
