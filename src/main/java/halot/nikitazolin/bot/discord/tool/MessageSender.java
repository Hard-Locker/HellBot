package halot.nikitazolin.bot.discord.tool;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.localization.action.PermissionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageSender {

  private final MessageFormatter messageFormatter;
  private final PermissionProvider permissionProvider;

  public void sendPrivateMessage(User user, MessageCreateData messageCreateData) {
    MessageCreateData fullMessageCreateData = new MessageCreateBuilder().setContent(messageCreateData.getContent())
        .build();

    if (!user.isBot()) {
      user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(fullMessageCreateData).queue(),
          error -> log.warn("Failed to send a private message to the user: " + user));
    }
  }

  public void sendPrivateMessage(User user, EmbedBuilder embedBuilder) {
    MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embedBuilder.build()).build();

    if (!user.isBot()) {
      user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageCreateData).queue(),
          error -> log.warn("Failed to send a private message to the user: " + user));
    }
  }

  public void sendPrivateMessageAccessError(User user) {
    EmbedBuilder embed = messageFormatter.createAltInfoEmbed(permissionProvider.getText("command.denied"));
    MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embed.build()).build();

    if (!user.isBot()) {
      user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageCreateData).queue(),
          error -> log.warn("Failed to send a private message to the user: " + user));
    }
  }

  public void sendText(TextChannel textChannel, CharSequence text) {
    textChannel.sendMessage(text).queue();
  }

  public void sendMessage(TextChannel textChannel, MessageCreateData messageCreateData) {
    textChannel.sendMessage(messageCreateData).queue();
  }

  public void sendMessageEmbed(TextChannel textChannel, EmbedBuilder embedBuilder) {
    MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embedBuilder.build()).build();

    textChannel.sendMessage(messageCreateData).queue();
  }

  public Long sendMessageWithActionRow(TextChannel textChannel, MessageCreateData messageCreateData,
      Collection<? extends ItemComponent> components) {
    CompletableFuture<Long> futureMessageId = new CompletableFuture<>();
    MessageCreateData fullMessageCreateData = new MessageCreateBuilder().setContent(messageCreateData.getContent())
        .setActionRow(components).build();

    textChannel.sendMessage(fullMessageCreateData).queue(messageSent -> {
      Long messageId = messageSent.getIdLong();
      futureMessageId.complete(messageId);
      log.debug("Message sent with ID: " + messageId);
    }, error -> {
      futureMessageId.complete(null);
      log.warn("Failed to send message: " + error.getMessage());
    });

    try {
      return futureMessageId.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Error waiting for the message to be sent: ", e);
      Thread.currentThread().interrupt();

      return null;
    }
  }

  public Long sendMessageWithActionRow(TextChannel textChannel, String message,
      Collection<? extends ItemComponent> components) {
    CompletableFuture<Long> futureMessageId = new CompletableFuture<>();
    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(message).setActionRow(components)
        .build();

    textChannel.sendMessage(messageCreateData).queue(messageSent -> {
      Long messageId = messageSent.getIdLong();
      futureMessageId.complete(messageId);
      log.debug("Message sent with ID: " + messageId);
    }, error -> {
      futureMessageId.complete(null);
      log.warn("Failed to send message: " + error.getMessage());
    });

    try {
      return futureMessageId.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Error waiting for the message to be sent: ", e);
      Thread.currentThread().interrupt();

      return null;
    }
  }

  public Long sendMessageWithActionRow(TextChannel textChannel, String content, List<ActionRow> actionRows) {
    CompletableFuture<Long> futureMessageId = new CompletableFuture<>();
    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(content).setComponents(actionRows)
        .build();

    textChannel.sendMessage(messageCreateData).queue(messageSent -> {
      Long messageId = messageSent.getIdLong();
      futureMessageId.complete(messageId);
      log.debug("Message sent with ID: " + messageId);
    }, error -> {
      futureMessageId.complete(null);
      log.warn("Failed to send message: " + error.getMessage());
    });

    try {
      return futureMessageId.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Error waiting for the message to be sent: ", e);
      Thread.currentThread().interrupt();
      return null;
    }
  }
}
