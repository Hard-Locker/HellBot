package halot.nikitazolin.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

@Service
public class Player {

  private String BOT_TOKEN;

  @PostConstruct
  public void init() throws Exception {
    readTokenFromFile();
    JDA api = JDABuilder.createDefault(BOT_TOKEN).build();
  }

  private void readTokenFromFile() throws IOException {
    BOT_TOKEN = new String(Files.readAllBytes(Paths.get("bot-token.txt"))).trim();
  }
  
  public void play() {
    // Your code here
  }
}
