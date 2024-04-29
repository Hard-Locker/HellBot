package halot.nikitazolin.bot.discord.action.model;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Getter
@RequiredArgsConstructor
public class ButtonMessage {

  private final Long messageId;
  private final List<Button> buttons;
  private final String commandName;
}
