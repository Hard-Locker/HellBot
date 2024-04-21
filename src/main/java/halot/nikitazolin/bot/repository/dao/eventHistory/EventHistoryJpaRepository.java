package halot.nikitazolin.bot.repository.dao.eventHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.EventHistory;

public interface EventHistoryJpaRepository extends JpaRepository<EventHistory, Long> {

}
