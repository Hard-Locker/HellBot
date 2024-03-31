package halot.nikitazolin.bot.repository.model;

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
@Table(name = "guild")
public class Guild {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "guild_id", nullable = false, unique = true)
  private Long guildId;

  @Column(name = "guild_name", nullable = false, columnDefinition = "varchar(255)")
  private String guildName;
}
