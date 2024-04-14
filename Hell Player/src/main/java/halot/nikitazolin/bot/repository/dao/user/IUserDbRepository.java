package halot.nikitazolin.bot.repository.dao.user;

import java.util.List;
import java.util.Optional;

import halot.nikitazolin.bot.repository.model.UserDb;

public interface IUserDbRepository {

  boolean insert(UserDb userDb);

  boolean update(UserDb userDb);

  boolean delete(Long userId);

  List<UserDb> getAll();
  
  Optional<UserDb> findByUserId(Long userId);
}
