package halot.nikitazolin.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import halot.nikitazolin.bot.slashCommands.manager.SlashCommandHandler;
import halot.nikitazolin.bot.listeners.ReadyListener;

@Component
public class JdaService {

//  private String BOT_TOKEN;
//  private static JDA api;
//
//  @PostConstruct
//  public void init() throws Exception {
//    readTokenFromFile();
//    api = JDABuilder.createDefault(BOT_TOKEN)
//        .setActivity(Activity.watching("на твою мамку"))
//        .addEventListeners(helloCommand)
//        .build();
//
//    Guild guild = api.getGuildById("702039161867206697");
//
//    if (guild != null) {
//      guild.upsertCommand("hello", "Say bot hello").queue();
//    }
//    
//    System.out.println("api.getStatus: " + api.getStatus());
//  }
//  
//  private void readTokenFromFile() throws IOException {
//    BOT_TOKEN = new String(Files.readAllBytes(Paths.get("src/main/resources/bot-token.txt"))).trim();
//  }

//  private final String token = "MTE2Mzg0NzMxNzkwOTE0NzcyOQ.GLFWdC.JRH5b-ceOvg-IyYOt9FdAE9T0QinCfdA3ZAY-Q";
  private final String token = "MTE2MjgyNjg0MzA3NDg1NDk1Mw.GQBtjs.IAjHBCpMVgkIiQn2AQj_dzu-iZWD4dmYeV8dv0";

  private final List<GatewayIntent> gatewayIntents = new ArrayList<>();
  private static JDA jda;

  public JdaService() {
//    this.token = token;

    start(Collections.emptyList());
  }

  /**
   * @param gatewayIntents the gatewayIntents to set -
   *                       https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/requests/GatewayIntent.html.
   *                       If you want none, just pass an empty List ->
   *                       Collections.emptyList()
   *
   */
  private void start(List<GatewayIntent> gatewayIntents) {
    try {
      jda = JDABuilder.createDefault(token).enableIntents(gatewayIntents)
          .addEventListeners(new SlashCommandHandler(), new ReadyListener()).build();

      jda.awaitReady();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  public Optional<JDA> getJda() {
    return Optional.ofNullable(jda);
  }
}
