package halot.nikitazolin.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.commands.HelloCommand;
import halot.nikitazolin.bot.util.InputNumber;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

@Service
public class Player implements EventListener{

  private String BOT_TOKEN;
  private static JDA api;
//  private final HelloCommand helloCommand;

//  @Autowired
//  public Player(HelloCommand helloCommand) {
//    this.helloCommand = helloCommand;
//  }
  

  @PostConstruct
  public void init() throws Exception {
    readTokenFromFile();
    api = JDABuilder.createDefault(BOT_TOKEN)
        .setActivity(Activity.watching("на твою мамку"))
//        .addEventListeners(helloCommand)
        .addEventListeners(new HelloCommand())
        .build();

//    Guild guild = api.getGuildById("702039161867206697");
//
//    if (guild != null) {
//      guild.upsertCommand("hello", "Say bot hello").queue();
//    }
  }
  
  @Override
  public void onEvent(GenericEvent event) {
    Guild guild = api.getGuildById("702039161867206697");
    
    if (guild != null) {
      guild.upsertCommand("hello", "Say bot hello").queue();
    }
  }

  private void readTokenFromFile() throws IOException {
    BOT_TOKEN = new String(Files.readAllBytes(Paths.get("src/main/resources/bot-token.txt"))).trim();
  }

  public void play() {
//    int selectedOption;
//    
//    while (true) {
//      System.out.println("Enter 0 for exit");
//      selectedOption = inputNumber.readInputNumber("Enter number: ");
//
//      switch (selectedOption) {
//      case 0:
//        return;
//
//      default:
//        System.err.println("Select only the items listed");
//        break;
//      }
//    }
  }

  
//  @Override
//  public void onReadyEvent(ReadyEvent event) {
//      // Code goes here...
//  }
}
