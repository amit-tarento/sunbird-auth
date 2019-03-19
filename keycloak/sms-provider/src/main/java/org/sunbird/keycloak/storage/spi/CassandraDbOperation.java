package org.sunbird.keycloak.storage.spi;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

public class CassandraDbOperation {

  private CassandraConnection connection;
  private DecryptionService decryptionService = new DefaultDecryptionServiceImpl();
  private EncryptionService encryptionService = new DefaultEncryptionServivceImpl();

  public CassandraDbOperation(CassandraConnection connection) {
    this.connection = connection;
  }

  public int getUserCount() {

    ResultSet rs =
        this.connection.getSession().execute("select count(*) from sunbird.user limit 10");
    return rs.one().getInt(0);
  }

  public User getUser(String id) {

    ResultSet rs =
        this.connection.getSession().execute("select * from sunbird.user where id = '" + id + "'");
    Row r = rs.one();
    User user = new User(r.getString("id"), r.getString("firstname"), "");
    user.setEmail(r.getString("email"));
    return user;
  }

  public User getUserByName(String username) {
    try {

      Row r = getUserBy("email", username, true);
      if (r != null) return readUser(r);

      r = getUserBy("phone", username, true);
      if (r != null) return readUser(r);

      r = getUserBy("username", username, true);
      if (r != null) return readUser(r);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  private User readUser(Row r) {
    User user = new User(r.getString("id"), r.getString("firstname"), "");
    user.setUsername(decrypt(r.getString("username")));
    user.setEmail(decrypt(r.getString("email")));
    user.setPhone(decrypt(r.getString("phone")));
    return user;
  }

  private String decrypt(String data) {
    return decryptionService.decryptData(data);
  }

  private Row getUserBy(String key, String searchValue, boolean encrypt) {
    if (encrypt) {
      try {
        searchValue = encryptionService.encryptData(searchValue);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    ResultSet rs =
        this.connection
            .getSession()
            .execute("select * from sunbird.user where " + key + "='" + searchValue + "'");
    return rs.one();
  }
}
