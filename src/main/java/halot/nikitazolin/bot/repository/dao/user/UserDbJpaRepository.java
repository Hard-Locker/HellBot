package halot.nikitazolin.bot.repository.dao.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.UserDb;

public interface UserDbJpaRepository extends JpaRepository<UserDb, Long> {

  Optional<UserDb> findOneByUserId(Long userId);
}
