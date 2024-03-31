package halot.nikitazolin.bot.repository.dao.guild;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import halot.nikitazolin.bot.repository.model.Guild;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
//@Transactional
@Slf4j
public class GuildRepository implements IGuildRepository {

  private final GuildJpaRepository guildJpaRepository;

  @Override
  public boolean insert(Guild guild) {
    log.info("Inserting a new guild into the database: {}", guild);

    try {
      guildJpaRepository.save(guild);
      log.info("Inserted guild: {}", guild);

      return true;
    } catch (DataAccessException e) {
      log.error("Error inserting guild: {}", guild, e);

      return false;
    }
  }

  @Override
  public boolean update(Guild guild) {
    log.info("Updating guild in the database: {}", guild);

    try {
      guildJpaRepository.save(guild);
      log.info("Updated guild: {}", guild);

      return true;
    } catch (DataAccessException e) {
      log.error("Error updating guild: {}", guild, e);

      return false;
    }
  }

  @Override
  public boolean delete(Long guildId) {
    log.info("Deleting guild with ID: {}", guildId);

    try {
      guildJpaRepository.deleteById(guildId);
      log.info("Deleted guild with ID: {}", guildId);

      return true;
    } catch (DataAccessException e) {
      log.error("Error deleting guild with ID: {}", guildId, e);

      return false;
    }
  }

  @Override
  public List<Guild> getAll() {
    log.info("Retrieving all guilds from the database");

    try {
      List<Guild> guilds = guildJpaRepository.findAll();
      log.info("Retrieved {} guilds from database.", guilds.size());

      return guilds;
    } catch (DataAccessException e) {
      log.error("Error retrieving guilds from database", e);

      return Collections.emptyList();
    }
  }
}
