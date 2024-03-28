package halot.nikitazolin.bot.init.authorization.data;

import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseVendor {
  H2("H2 Database", new String[] { "H2", "h2", "H2 Database" }),
  POSTGRESQL("PostgreSQL", new String[] { "PostgreSQL", "POSTGRESQL", "PG", "pg", "Postgres", "POSTGRES", "postgres" });

  private final String displayName;
  private final String[] identifiers;

  public static Optional<DatabaseVendor> fromString(String text) {
    return Arrays.stream(DatabaseVendor.values()).filter(vendor -> Arrays.asList(vendor.identifiers).contains(text))
        .findFirst();
  }
}
