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
@Table(name = "song_history")
public class SongHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_datetime", nullable = false)
  private LocalDateTime eventDatetime;

  @Column(name = "song_url", nullable = false, columnDefinition = "varchar(255)")
  private String songUrl;

  @Column(name = "song_artist", columnDefinition = "varchar(255)")
  private String songArtist;

  @Column(name = "song_name", columnDefinition = "varchar(255)")
  private String songName;

  @Column(name = "song_duration")
  private Long songDuration;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "guild_id", nullable = false)
  private Long guildId;

  @Column(name = "note", columnDefinition = "varchar(255)")
  private String note;

  public SongHistory(LocalDateTime eventDatetime, String songUrl, String songArtist, String songName, Long songDuration) {
    super();
    this.eventDatetime = eventDatetime;
    this.songUrl = songUrl;
    this.songArtist = songArtist;
    this.songName = songName;
    this.songDuration = songDuration;
  }

  public SongHistory(LocalDateTime eventDatetime, String songUrl, String songArtist, String songName, Long songDuration, Long userId, Long guildId) {
    this.eventDatetime = eventDatetime;
    this.songUrl = songUrl;
    this.songArtist = songArtist;
    this.songName = songName;
    this.songDuration = songDuration;
    this.userId = userId;
    this.guildId = guildId;
  }
}
