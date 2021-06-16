package pro.gravit.launchserver.dao;

import java.util.List;
import java.util.UUID;

public interface UserDAO {
    @SuppressWarnings("unused")
    User findById(int id);

    User findByUsername(String username);

    User findByUUID(UUID uuid);

    void save(User user);

    void update(User user);

    @SuppressWarnings("unused")
    void delete(User user);

    List<User> findAll();
}
