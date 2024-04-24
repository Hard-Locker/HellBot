package halot.nikitazolin.bot.repository.dao.songHistory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.SongHistory;

public interface SongHistoryJpaRepository extends JpaRepository<SongHistory, Long> {

  List<SongHistory> findByEventDatetimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
