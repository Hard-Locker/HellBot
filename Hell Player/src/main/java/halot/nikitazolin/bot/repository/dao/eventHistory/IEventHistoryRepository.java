package halot.nikitazolin.bot.repository.dao.eventHistory;

import java.util.List;

import halot.nikitazolin.bot.repository.model.EventHistory;

public interface IEventHistoryRepository {

  boolean insert(EventHistory eventHistory);

  boolean update(EventHistory eventHistory);

  boolean delete(Long eventHistoryId);

  List<EventHistory> getAll();
}
