package invox.repository;

import invox.model.User;

import java.util.Optional;

public interface UserRepository extends Repository<User> {

    Optional<User> findByUsername(String username);
}
