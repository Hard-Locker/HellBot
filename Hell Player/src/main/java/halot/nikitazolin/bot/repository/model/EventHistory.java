package halot.nikitazolin.bot.repository.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "event_history")
public class EventHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_datetime", nullable = false)
  private LocalDateTime eventDatetime;

  @Column(name = "event_type", nullable = false, columnDefinition = "varchar(255)")
  private String eventType;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "guild_id", nullable = false)
  private Long guildId;

  @Column(name = "note", columnDefinition = "varchar(255)")
  private String note;
}
