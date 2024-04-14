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
public class GuildDb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "guild_id", nullable = false, unique = true)
  private Long guildId;

  @Column(name = "guild_name", nullable = false, columnDefinition = "varchar(255)")
  private String guildName;

  @Column(name = "note", nullable = true, columnDefinition = "varchar(255)")
  private String note;

  public GuildDb(Long guildId, String guildName) {
    super();
    this.guildId = guildId;
    this.guildName = guildName;
  }

  public GuildDb(Long guildId, String guildName, String note) {
    super();
    this.guildId = guildId;
    this.guildName = guildName;
    this.note = note;
  }
}
