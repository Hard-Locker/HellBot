package halot.nikitazolin.bot.repository.dao.songHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.SongHistory;

public interface SongHistoryJpaRepository extends JpaRepository<SongHistory, Long> {

}
