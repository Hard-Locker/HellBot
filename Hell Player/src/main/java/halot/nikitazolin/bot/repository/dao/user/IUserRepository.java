package halot.nikitazolin.bot.repository.dao.user;

import java.util.List;

import halot.nikitazolin.bot.repository.model.User;

public interface IUserRepository {

  boolean insert(User user);

  boolean update(User user);

  boolean delete(Long userId);

  List<User> getAll();
}
