package halot.nikitazolin.bot.repository.dao.eventHistory;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import halot.nikitazolin.bot.repository.model.EventHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
//@Transactional
@Slf4j
public class EventHistoryRepository implements IEventHistoryRepository {

  private final EventHistoryJpaRepository eventHistoryJpaRepository;

  @Override
  public boolean insert(EventHistory eventHistory) {
    log.info("Inserting a new eventHistory into the database: {}", eventHistory);

    try {
      eventHistoryJpaRepository.save(eventHistory);
      log.info("Inserted eventHistory: {}", eventHistory);

      return true;
    } catch (DataAccessException e) {
      log.error("Error inserting eventHistory: {}", eventHistory, e);

      return false;
    }
  }

  @Override
  public boolean update(EventHistory eventHistory) {
    log.info("Updating eventHistory in the database: {}", eventHistory);

    try {
      eventHistoryJpaRepository.save(eventHistory);
      log.info("Updated eventHistory: {}", eventHistory);

      return true;
    } catch (DataAccessException e) {
      log.error("Error updating eventHistory: {}", eventHistory, e);

      return false;
    }
  }

  @Override
  public boolean delete(Long eventHistoryId) {
    log.info("Deleting eventHistory with ID: {}", eventHistoryId);

    try {
      eventHistoryJpaRepository.deleteById(eventHistoryId);
      log.info("Deleted eventHistory with ID: {}", eventHistoryId);

      return true;
    } catch (DataAccessException e) {
      log.error("Error deleting eventHistory with ID: {}", eventHistoryId, e);

      return false;
    }
  }

  @Override
  public List<EventHistory> getAll() {
    log.info("Retrieving all eventHistorys from the database");

    try {
      List<EventHistory> eventHistorys = eventHistoryJpaRepository.findAll();
      log.info("Retrieved {} eventHistorys from database.", eventHistorys.size());

      return eventHistorys;
    } catch (DataAccessException e) {
      log.error("Error retrieving eventHistorys from database", e);

      return Collections.emptyList();
    }
  }
}
