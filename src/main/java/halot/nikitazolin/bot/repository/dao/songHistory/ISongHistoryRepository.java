package halot.nikitazolin.bot.repository.dao.songHistory;

import java.util.List;

import halot.nikitazolin.bot.repository.model.SongHistory;

public interface ISongHistoryRepository {

  boolean insert(SongHistory songHistory);

  boolean update(SongHistory songHistory);

  boolean delete(Long songHistoryId);

  List<SongHistory> getAll();
}
