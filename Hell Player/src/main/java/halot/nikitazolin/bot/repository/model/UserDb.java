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
@Table(name = "users")
public class UserDb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "username", nullable = true, columnDefinition = "varchar(255)")
  private String username;

  @Column(name = "role", nullable = true, columnDefinition = "varchar(255)")
  private String role;

  @Column(name = "permission", nullable = true, columnDefinition = "varchar(255)")
  private String permission;

  @Column(name = "note", nullable = true, columnDefinition = "varchar(255)")
  private String note;

  public UserDb(Long userId, String username) {
    this.userId = userId;
    this.username = username;
  }

  public UserDb(Long userId, String username, String role, String permission) {
    this.userId = userId;
    this.username = username;
    this.role = role;
    this.permission = permission;
  }

  public UserDb(Long userId, String username, String role, String permission, String note) {
    this.userId = userId;
    this.username = username;
    this.role = role;
    this.permission = permission;
    this.note = note;
  }
}
