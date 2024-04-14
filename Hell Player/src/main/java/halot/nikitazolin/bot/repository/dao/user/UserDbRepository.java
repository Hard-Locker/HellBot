package halot.nikitazolin.bot.repository.dao.user;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import halot.nikitazolin.bot.repository.model.UserDb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDbRepository implements IUserDbRepository {

  private final UserDbJpaRepository userDbJpaRepository;

  @Override
  public boolean insert(UserDb userDb) {
    log.info("Inserting a new user into the database: {}", userDb);

    try {
      userDbJpaRepository.save(userDb);
      log.info("Inserted user: {}", userDb);

      return true;
    } catch (DataAccessException e) {
      log.error("Error inserting user: {}", userDb, e);

      return false;
    }
  }

  @Override
  public boolean update(UserDb userDb) {
    log.info("Updating user in the database: {}", userDb);

    try {
      userDbJpaRepository.save(userDb);
      log.info("Updated user: {}", userDb);

      return true;
    } catch (DataAccessException e) {
      log.error("Error updating user: {}", userDb, e);

      return false;
    }
  }

  @Override
  public boolean delete(Long userId) {
    log.info("Deleting user with ID: {}", userId);

    try {
      userDbJpaRepository.deleteById(userId);
      log.info("Deleted user with ID: {}", userId);

      return true;
    } catch (DataAccessException e) {
      log.error("Error deleting user with ID: {}", userId, e);

      return false;
    }
  }

  @Override
  public List<UserDb> getAll() {
    log.info("Retrieving all users from the database");

    try {
      List<UserDb> userDbs = userDbJpaRepository.findAll();
      log.info("Retrieved {} users from database.", userDbs.size());

      return userDbs;
    } catch (DataAccessException e) {
      log.error("Error retrieving users from database", e);

      return Collections.emptyList();
    }
  }

  @Override
  public Optional<UserDb> findByUserId(Long userId) {
    log.info("Retrieving user from the database by ID: {}", userId);

    try {
      Optional<UserDb> userDb = userDbJpaRepository.findOneByUserId(userId);
      log.info("Retrieved user from database? = {}", userDb.isPresent());

      return userDb;
    } catch (DataAccessException e) {
      log.error("Error retrieving user from database", e);

      return Optional.empty();
    }
  }
}
