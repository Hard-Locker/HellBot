package halot.nikitazolin.bot.repository.dao.guild;

import java.util.List;

import halot.nikitazolin.bot.repository.model.Guild;

public interface IGuildRepository {

  boolean insert(Guild guild);

  boolean update(Guild guild);

  boolean delete(Long guildId);

  List<Guild> getAll();
}
