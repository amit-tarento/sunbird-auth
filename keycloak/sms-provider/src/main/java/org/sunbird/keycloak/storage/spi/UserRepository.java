package org.sunbird.keycloak.storage.spi;

import java.util.Arrays;
import java.util.List;

class UserRepository {

  private CassandraDbOperation cassandraOperation;

  public UserRepository() {
   CassandraConnection connection  = new CassandraConnection();
   connection.connect("localhost", 9042);
   cassandraOperation = new CassandraDbOperation(connection);
  }

  public int getUsersCount() {
    return cassandraOperation.getUserCount();
  }

  public User findUserById(String id) {
    return cassandraOperation.getUser(id);
  }

  public User findUserByUsernameOrEmail(String username) {
    return cassandraOperation.getUserByName(username);
  }

  public List<User> findUsers(String query) {
    return Arrays.asList(cassandraOperation.getUserByName(query));
  }
}
