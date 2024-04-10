package halot.nikitazolin.bot.repository.dao.guild;

import java.util.List;
import java.util.Optional;

import halot.nikitazolin.bot.repository.model.GuildDb;

public interface IGuildDbRepository {

  boolean insert(GuildDb guildDb);

  boolean update(GuildDb guildDb);

  boolean delete(Long guildId);

  List<GuildDb> getAll();

  Optional<GuildDb> findByGuildId(Long guildId);
}
