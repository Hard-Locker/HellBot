package halot.nikitazolin.bot.init.discord;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.repository.dao.eventHistory.IEventHistoryRepository;
import halot.nikitazolin.bot.repository.dao.guild.IGuildDbRepository;
import halot.nikitazolin.bot.repository.dao.songHistory.ISongHistoryRepository;
import halot.nikitazolin.bot.repository.dao.user.IUserDbRepository;
import halot.nikitazolin.bot.repository.model.GuildDb;
import halot.nikitazolin.bot.repository.model.UserDb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbFiller {

  private final JdaMaker jdaMaker;
  private final IGuildDbRepository guildDbRepository;
  private final IUserDbRepository userDbRepository;
  private final IEventHistoryRepository eventHistoryRepository;
  private final ISongHistoryRepository songHistoryRepository;

  public void fillDatabase() {
    Guild guild = jdaMaker.getJda().get().getGuilds().getFirst();
    fillGuildTable(guild);
  }

  public void fillGuildTable(Guild guild) {
    GuildDb guildDB = new GuildDb(guild.getIdLong(), guild.getName());
    Optional<GuildDb> existingGuildDb = guildDbRepository.findByGuildId(guild.getIdLong());

    if (existingGuildDb.isEmpty()) {
      guildDbRepository.insert(guildDB);
      log.info("New guild saved to DB.");
    } else {
      log.info("Guild already exists in database. No action needed.");
    }
  }

  @Transactional
  public void fillUserTable(Member member) {
    Optional<UserDb> existingUserDb = userDbRepository.findByUserId(member.getUser().getIdLong());
    UserDb userDb = new UserDb(member.getUser().getIdLong(), member.getUser().getName());

    if (existingUserDb.isEmpty()) {
      userDbRepository.insert(userDb);
      log.info("User {} saved to DB.", member.getUser().getIdLong());
    } else {
      log.info("User {} already exists in database.", member.getUser().getIdLong());
    }
  }
}
