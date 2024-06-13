package halot.nikitazolin.bot.discord.action.model;

import java.util.HashMap;
import java.util.Map;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ActionMessage {

  private final Long messageId;
  private final String commandName;
  private final long expireTime;
  private BotCommandContext context;
  private Map<String, Object> additionalData = new HashMap<>();

  public ActionMessage(Long messageId, String commandName, long expireTime, BotCommandContext context) {
    this.messageId = messageId;
    this.commandName = commandName;
    this.expireTime = expireTime;
    this.context = context;
  }

  public ActionMessage(Long messageId, String commandName, long expireTime, BotCommandContext context, Map<String, Object> additional) {
    this.messageId = messageId;
    this.commandName = commandName;
    this.expireTime = expireTime;
    this.context = context;
    this.additionalData = additional;
  }
}
