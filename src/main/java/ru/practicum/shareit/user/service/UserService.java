package ru.practicum.shareit.user.service;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    @NonNull
    User createUser(@NonNull User user);

    Optional<User> updateUser(@NonNull User user);

    Optional<User> getUserById(long id);

    @NonNull
    void removeUserById(long id);

    @NonNull
    List<User> getUserList();
}
