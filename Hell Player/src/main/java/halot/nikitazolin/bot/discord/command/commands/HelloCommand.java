package halot.nikitazolin.bot.discord.command.commands;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.command.model.BotCommand;
import halot.nikitazolin.bot.discord.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class HelloCommand extends BotCommand {

  private final MessageUtil messageUtil;
  
  @Override
  public String name() {
    return "hello";
  }
  
  @Override
  public List<String> nameAliases() {
    return List.of("hello");
  }

  @Override
  public List<String> commandPrefixes() {
    return List.of("!", "1");
  }
  
  @Override
  public String description() {
    return "Greetings";
  }
  
  @Override
  public String requiredRole() {
    return null;
  }

  @Override
  public Permission neededPermission() {
    return Permission.USE_APPLICATION_COMMANDS;
  }

  @Override
  public boolean guildOnly() {
    return true;
  }

  @Override
  public OptionData[] options() {
    return new OptionData[] {};
  }

  @Override
  public void execute(BotCommandContext context) {
    EmbedBuilder embed = messageUtil.createAltInfoEmbed(context.getUser().getAsMention() + " Gamarjoba genacvale!");
    
    context.sendMessageEmbed(embed);
    
    log.debug("User get hello" + context.getUser());
  }
}
