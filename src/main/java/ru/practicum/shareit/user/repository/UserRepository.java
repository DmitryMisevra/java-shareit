package ru.practicum.shareit.user.repository;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {


    User createUser(User user);

    Optional<User> updateUser(@NonNull User user);

    Optional<User> getUserById(long id);

    void removeUserById(long id);

    List<User> getUserList();
}
