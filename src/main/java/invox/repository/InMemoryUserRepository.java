package invox.repository;

import invox.exception.EntityNotFoundException;
import invox.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User add(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) throws EntityNotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("User", user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(int id) throws EntityNotFoundException {
        if (users.remove(id) == null) {
            throw new EntityNotFoundException("User", id);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().equals(username))
                .findFirst();
    }
}
