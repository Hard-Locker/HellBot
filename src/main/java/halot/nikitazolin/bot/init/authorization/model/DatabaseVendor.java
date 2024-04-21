package halot.nikitazolin.bot.init.authorization.model;

import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseVendor {
  H2("H2 Database", new String[] { "H2", "h2", "H2 Database" }, "org.h2.Driver"),
  POSTGRESQL("PostgreSQL", new String[] { "PostgreSQL", "POSTGRESQL", "PG", "pg", "Postgres", "POSTGRES", "postgres" }, "org.postgresql.Driver"),
  FIREBIRD("Firebird", new String[] { "Firebird", "FIREBIRD", "firebird", "fb", "FB"}, "org.firebirdsql.jdbc.FBDriver");

  private final String name;
  private final String[] nameAliases;
  private final String driverClassName;

  public static Optional<DatabaseVendor> fromString(String text) {
    return Arrays.stream(DatabaseVendor.values()).filter(vendor -> Arrays.asList(vendor.nameAliases).contains(text)).findFirst();
  }
}
