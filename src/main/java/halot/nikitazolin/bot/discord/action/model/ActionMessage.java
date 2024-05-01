package halot.nikitazolin.bot.discord.action.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ActionMessage {

  private final Long messageId;
  private final String commandName;
}
