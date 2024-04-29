package halot.nikitazolin.bot.discord.action.command.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.ButtonMessageCollector;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.action.model.ButtonMessage;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetSettingsCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final ButtonMessageCollector buttonMessageCollector;

  private final String commandName = "set";

  @Override
  public String name() {
    return commandName;
  }

  @Override
  public List<String> nameAliases() {
    List<String> defaultAliases = List.of(commandName);
    List<String> additionalAliases = settings.getNameAliases().getOrDefault(commandName, List.of());
    List<String> allAliases = new ArrayList<>(defaultAliases);
    allAliases.addAll(additionalAliases);

    return allAliases;
  }

  @Override
  public List<String> commandPrefixes() {
    List<String> prefixes = new ArrayList<>(List.of("!"));
    List<String> additionalPrefixes = settings.getPrefixes() != null ? settings.getPrefixes() : List.of();
    prefixes.addAll(additionalPrefixes);

    return prefixes;
  }

  @Override
  public String description() {
    return "You can change any settings";
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
    Button yesButton = Button.primary("yesButton", "Yes");
    Button noButton = Button.danger("noButton", "No");
    List<Button> buttons = List.of(yesButton, noButton);

    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), "Are you sure you want to proceed?", buttons);
    buttonMessageCollector.addButtonMessage(messageId, new ButtonMessage(messageId, buttons, commandName));
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    String componentId = buttonEvent.getComponentId();
    Message originalMessage = buttonEvent.getMessage();

    switch (componentId) {
    case "yesButton":
      buttonEvent.reply("You clicked Yes!").setEphemeral(true).queue();
      originalMessage.delete().queue();
      log.info("Clicked yesButton");
      break;
    case "noButton":
      buttonEvent.reply("You clicked No!").setEphemeral(true).queue();
      originalMessage.delete().queue();
      log.info("Clicked noButton");
      break;
    default:
      buttonEvent.reply("Unknown button").setEphemeral(true).queue();
      log.info("Clicked unknown button");
      break;
    }
  }
}