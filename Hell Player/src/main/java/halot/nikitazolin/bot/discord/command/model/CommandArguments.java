package halot.nikitazolin.bot.discord.command.model;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message.Attachment;

@Component
@Getter
@RequiredArgsConstructor
public class CommandArguments {
  
  private final List<String> string;
//  private final List<Integer> integer;
  private final List<Attachment> attachment;

}
