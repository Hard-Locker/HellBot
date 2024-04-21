package halot.nikitazolin.bot.repository.dao.songHistory;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import halot.nikitazolin.bot.repository.model.SongHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SongHistoryRepository implements ISongHistoryRepository {

  private final SongHistoryJpaRepository songHistoryJpaRepository;

  @Override
  public boolean insert(SongHistory songHistory) {
    log.info("Inserting a new songHistory into the database: {}", songHistory);

    try {
      songHistoryJpaRepository.save(songHistory);
      log.info("Inserted songHistory: {}", songHistory);

      return true;
    } catch (DataAccessException e) {
      log.error("Error inserting songHistory: {}", songHistory, e);

      return false;
    }
  }

  @Override
  public boolean update(SongHistory songHistory) {
    log.info("Updating songHistory in the database: {}", songHistory);

    try {
      songHistoryJpaRepository.save(songHistory);
      log.info("Updated songHistory: {}", songHistory);

      return true;
    } catch (DataAccessException e) {
      log.error("Error updating songHistory: {}", songHistory, e);

      return false;
    }
  }

  @Override
  public boolean delete(Long songHistoryId) {
    log.info("Deleting songHistory with ID: {}", songHistoryId);

    try {
      songHistoryJpaRepository.deleteById(songHistoryId);
      log.info("Deleted songHistory with ID: {}", songHistoryId);

      return true;
    } catch (DataAccessException e) {
      log.error("Error deleting songHistory with ID: {}", songHistoryId, e);

      return false;
    }
  }

  @Override
  public List<SongHistory> getAll() {
    log.info("Retrieving all songHistorys from the database");

    try {
      List<SongHistory> songHistorys = songHistoryJpaRepository.findAll();
      log.info("Retrieved {} songHistorys from database.", songHistorys.size());

      return songHistorys;
    } catch (DataAccessException e) {
      log.error("Error retrieving songHistorys from database", e);

      return Collections.emptyList();
    }
  }
}
