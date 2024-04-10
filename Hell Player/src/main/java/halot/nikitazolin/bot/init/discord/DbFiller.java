package halot.nikitazolin.bot.init.discord;

import java.util.Optional;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.repository.dao.guild.IGuildDbRepository;
import halot.nikitazolin.bot.repository.dao.user.IUserDbRepository;
import halot.nikitazolin.bot.repository.model.GuildDb;
import halot.nikitazolin.bot.repository.model.UserDb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbFiller {

  private final JdaMaker jdaMaker;
  private final IGuildDbRepository guildDbRepository;
  private final IUserDbRepository userDbRepository;

  public void fillDatabase() {
    fillGuildTable();
    fillUserTable();
  }

  private void fillGuildTable() {
    Guild guild = jdaMaker.getJda().get().getGuilds().getFirst();
    GuildDb guildDB = new GuildDb(guild.getIdLong(), guild.getName());
    Optional<GuildDb> existingGuildDb = guildDbRepository.findByGuildId(guild.getIdLong());

    if (existingGuildDb.isEmpty()) {
      guildDbRepository.insert(guildDB);
      log.info("New guild saved to DB.");
    } else {
      log.info("Guild already exists in database. No action needed.");
    }
  }
  
  private void fillUserTable() {
    Guild guild = jdaMaker.getJda().get().getGuilds().getFirst();
//    List<Member> members = guild.getMembers();
    
    System.out.println("Member count: " + guild.getMemberCount());
    
//    for(Member member : members) {
//      System.out.println(member.getNickname());
//    }
    
    UserDb userDb = new UserDb();
  }
}
