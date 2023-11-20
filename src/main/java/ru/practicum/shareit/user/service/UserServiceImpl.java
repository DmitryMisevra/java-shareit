package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    @NonNull
    public User createUser(@NonNull User user) {
        return userRepository.createUser(user);
    }

    @Override
    public Optional<User> updateUser(@NonNull User user) {
        if (isEmailUnique(user)) {
            return userRepository.updateUser(user);
        } else {
            throw new EmailAlreadyExistsException("Email найден у другого пользователя");
        }
    }

    @Override
    @NonNull
    public Optional<User> getUserById(long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void removeUserById(long id) {
        userRepository.removeUserById(id);
    }

    @Override
    @NonNull
    public List<User> getUserList() {
        return userRepository.getUserList();
    }

    private boolean isEmailUnique(User user) {
        return getUserList().stream()
                .noneMatch(us -> us.getEmail().equalsIgnoreCase(user.getEmail()) && !us.getId().equals(user.getId()));
    }
}
