package halot.nikitazolin.bot.init.authorization.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Database {

  private boolean dbEnabled;
  private DatabaseVendor dbVendor;
  private String dbName;
  private String dbUrl;
  private String dbUsername;
  private String dbPassword;
}
