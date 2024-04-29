package halot.nikitazolin.bot.discord.action.model;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message.Attachment;

@Getter
@RequiredArgsConstructor
public class CommandArguments {

  private final List<String> string;
  private final List<Attachment> attachment;
}
