package halot.nikitazolin.bot.repository.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.UserDb;

public interface UserDbJpaRepository extends JpaRepository<UserDb, Long> {

}
