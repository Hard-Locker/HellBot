package halot.nikitazolin.bot.repository.dao.guild;

import org.springframework.data.jpa.repository.JpaRepository;

import halot.nikitazolin.bot.repository.model.Guild;

public interface GuildJpaRepository extends JpaRepository<Guild, Long> {

}
