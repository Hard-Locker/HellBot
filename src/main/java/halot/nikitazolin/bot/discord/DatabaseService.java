package halot.nikitazolin.bot.discord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.command.BotCommandContext;
import halot.nikitazolin.bot.repository.dao.eventHistory.IEventHistoryRepository;
import halot.nikitazolin.bot.repository.dao.guild.IGuildDbRepository;
import halot.nikitazolin.bot.repository.dao.songHistory.ISongHistoryRepository;
import halot.nikitazolin.bot.repository.dao.user.IUserDbRepository;
import halot.nikitazolin.bot.repository.model.EventHistory;
import halot.nikitazolin.bot.repository.model.GuildDb;
import halot.nikitazolin.bot.repository.model.SongHistory;
import halot.nikitazolin.bot.repository.model.UserDb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseService {

  private final IGuildDbRepository guildDbRepository;
  private final IUserDbRepository userDbRepository;
  private final IEventHistoryRepository eventHistoryRepository;
  private final ISongHistoryRepository songHistoryRepository;

  public void saveGuildToDb(Guild guild) {
    GuildDb guildDB = new GuildDb(guild.getIdLong(), guild.getName());
    Optional<GuildDb> existingGuildDb = guildDbRepository.findByGuildId(guild.getIdLong());

    if (existingGuildDb.isEmpty()) {
      guildDbRepository.insert(guildDB);
      log.debug("New guild saved to DB.");
    } else {
      log.debug("Guild already exists in database. No action needed.");
    }
  }

  public void saveUserToDb(Member member) {
    Optional<UserDb> existingUserDb = userDbRepository.findByUserId(member.getUser().getIdLong());
    UserDb userDb = new UserDb(member.getUser().getIdLong(), member.getUser().getName());

    if (existingUserDb.isEmpty()) {
      userDbRepository.insert(userDb);
      log.debug("User {} saved to DB.", member.getUser().getIdLong());
    } else {
      log.debug("User {} already exists in database.", member.getUser().getIdLong());
    }
  }
  
  public void saveEventHistoryToDb(BotCommandContext context) {
    EventHistory eventHistory = new EventHistory(LocalDateTime.now(), context.getBotCommand().name(), context.getUser().getIdLong(), context.getGuild().getIdLong());
    
    eventHistoryRepository.insert(eventHistory);
    log.debug("EventHistory {} saved to DB.");
  }
  
  public void saveSongHistoryToDb(SongHistory songHistory) {
    songHistoryRepository.insert(songHistory);
    log.debug("SongHistory {} saved to DB.");
  }
  
  public List<SongHistory> getSongHistoryByDate(LocalDate date) {
    List<SongHistory> songHistorys = songHistoryRepository.getEventByDate(date);
    log.debug("SongHistory get from DB.");
    
    return songHistorys;
  }
}
