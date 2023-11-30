package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private long counter = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        final User createdUser = user.copyOf();
        createdUser.setId(counter);
        users.put(createdUser.getId(), createdUser);
        counter++;
        return users.get(createdUser.getId());
    }

    @Override
    public Optional<User> updateUser(@NonNull User user) {
        return Optional.ofNullable(users.computeIfPresent(user.getId(), (existingId, existingUser) -> {
            existingUser.updateWith(user);
            return existingUser.copyOf();
        }));
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id))
                .map(User::copyOf);
    }

    @Override
    public void removeUserById(long id) {
        users.remove(id);
    }

    @Override
    public List<User> getUserList() {
        return users.values()
                .stream()
                .map(User::copyOf)
                .collect(Collectors.toList());
    }
}
