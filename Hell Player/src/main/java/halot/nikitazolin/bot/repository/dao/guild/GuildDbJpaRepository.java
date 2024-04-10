package halot.nikitazolin.bot.repository.dao.guild;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.GuildDb;

public interface GuildDbJpaRepository extends JpaRepository<GuildDb, Long> {

  Optional<GuildDb> findOneByGuildId(Long guildId);
}
