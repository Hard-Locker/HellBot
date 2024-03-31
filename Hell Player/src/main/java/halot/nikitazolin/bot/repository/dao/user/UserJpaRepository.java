package halot.nikitazolin.bot.repository.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.User;

public interface UserJpaRepository extends JpaRepository<User, Long>{

}
