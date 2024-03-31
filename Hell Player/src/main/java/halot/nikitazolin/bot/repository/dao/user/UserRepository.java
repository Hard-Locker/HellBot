package halot.nikitazolin.bot.repository.dao.user;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import halot.nikitazolin.bot.repository.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
//@Transactional
@Slf4j
public class UserRepository implements IUserRepository {

  private final UserJpaRepository userJpaRepository;

  @Override
  public boolean insert(User user) {
    log.info("Inserting a new user into the database: {}", user);

    try {
      userJpaRepository.save(user);
      log.info("Inserted user: {}", user);

      return true;
    } catch (DataAccessException e) {
      log.error("Error inserting user: {}", user, e);

      return false;
    }
  }

  @Override
  public boolean update(User user) {
    log.info("Updating user in the database: {}", user);

    try {
      userJpaRepository.save(user);
      log.info("Updated user: {}", user);

      return true;
    } catch (DataAccessException e) {
      log.error("Error updating user: {}", user, e);

      return false;
    }
  }

  @Override
  public boolean delete(Long userId) {
    log.info("Deleting user with ID: {}", userId);

    try {
      userJpaRepository.deleteById(userId);
      log.info("Deleted user with ID: {}", userId);

      return true;
    } catch (DataAccessException e) {
      log.error("Error deleting user with ID: {}", userId, e);

      return false;
    }
  }

  @Override
  public List<User> getAll() {
    log.info("Retrieving all users from the database");

    try {
      List<User> users = userJpaRepository.findAll();
      log.info("Retrieved {} users from database.", users.size());

      return users;
    } catch (DataAccessException e) {
      log.error("Error retrieving users from database", e);

      return Collections.emptyList();
    }
  }
}
