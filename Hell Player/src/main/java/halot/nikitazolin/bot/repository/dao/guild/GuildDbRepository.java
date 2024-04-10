package halot.nikitazolin.bot.repository.dao.guild;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import halot.nikitazolin.bot.repository.model.GuildDb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GuildDbRepository implements IGuildDbRepository {

  private final GuildDbJpaRepository guildDbJpaRepository;

  @Override
  public boolean insert(GuildDb guildDb) {
    log.info("Inserting a new guild into the database: {}", guildDb);

    try {
      guildDbJpaRepository.save(guildDb);
      log.info("Inserted guild: {}", guildDb);

      return true;
    } catch (DataAccessException e) {
      log.error("Error inserting guild: {}", guildDb, e);

      return false;
    }
  }

  @Override
  public boolean update(GuildDb guildDb) {
    log.info("Updating guild in the database: {}", guildDb);

    try {
      guildDbJpaRepository.save(guildDb);
      log.info("Updated guild: {}", guildDb);

      return true;
    } catch (DataAccessException e) {
      log.error("Error updating guild: {}", guildDb, e);

      return false;
    }
  }

  @Override
  public boolean delete(Long guildId) {
    log.info("Deleting guild with ID: {}", guildId);

    try {
      guildDbJpaRepository.deleteById(guildId);
      log.info("Deleted guild with ID: {}", guildId);

      return true;
    } catch (DataAccessException e) {
      log.error("Error deleting guild with ID: {}", guildId, e);

      return false;
    }
  }

  @Override
  public List<GuildDb> getAll() {
    log.info("Retrieving all guilds from the database");

    try {
      List<GuildDb> guildDbs = guildDbJpaRepository.findAll();
      log.info("Retrieved {} guilds from database.", guildDbs.size());

      return guildDbs;
    } catch (DataAccessException e) {
      log.error("Error retrieving guilds from database", e);

      return Collections.emptyList();
    }
  }

  @Override
  public Optional<GuildDb> findByGuildId(Long guildId) {
    log.info("Retrieving guild from the database by ID.");

    try {
      Optional<GuildDb> guildDb = guildDbJpaRepository.findOneByGuildId(guildId);
      log.info("Retrieved guild from database.", guildDb);

      return guildDb;
    } catch (DataAccessException e) {
      log.error("Error retrieving guilds from database", e);

      return Optional.empty();
    }
  }
}
