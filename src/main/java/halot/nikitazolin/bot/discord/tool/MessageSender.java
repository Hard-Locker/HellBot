package halot.nikitazolin.bot.discord.tool;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Slf4j
public class MessageSender {

  public void sendText(TextChannel textChannel, CharSequence text) {
    textChannel.sendMessage(text).queue();
  }

  public void sendMessage(TextChannel textChannel, MessageCreateData messageCreateData) {
    textChannel.sendMessage(messageCreateData).queue();
  }

  public void sendMessagesEmbed(TextChannel textChannel, List<MessageEmbed> messagesEmbed) {
    textChannel.sendMessageEmbeds(messagesEmbed).queue();
  }

  public void sendMessageEmbed(TextChannel textChannel, EmbedBuilder embedBuilder) {
    MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embedBuilder.build()).build();

    textChannel.sendMessage(messageCreateData).queue();
  }

  public Long sendMessageWithButtons(TextChannel textChannel, String message, List<Button> buttons) {
    CompletableFuture<Long> futureMessageId = new CompletableFuture<>();
    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(message).setActionRow(buttons).build();

    textChannel.sendMessage(messageCreateData).queue(messageSent -> {
      Long messageId = messageSent.getIdLong();
      log.debug("Message sent with ID: " + messageId);
      futureMessageId.complete(messageId);
    }, error -> {
      log.error("Failed to send message: " + error.getMessage());
      futureMessageId.complete(null);
    });

    try {
      return futureMessageId.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Error waiting for the message to be sent: ", e);
      Thread.currentThread().interrupt();

      return null;
    }
  }

  public void sendPrivateMessage(User user, EmbedBuilder embedBuilder) {
    MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embedBuilder.build()).build();

    if (!user.isBot()) {
      user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(messageCreateData).queue(),
          error -> log.warn("Failed to send a private message to the user: " + user));
    }
  }
}
