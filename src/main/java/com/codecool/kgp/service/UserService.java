package com.codecool.kgp.service;

import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        List<User> filterUsers = users.stream().filter(user -> !user.getEmail().startsWith("deleted-")).toList();
        log.info("{} users was found", filterUsers.size());
        return filterUsers;
    }

    public User getUser(UUID id) {
        User user = findUser(id);
        log.info("User with id '{}' was found", id);
        return user;
    }

    public int getUserScore(UUID id) {
        User user = findUser(id);
        List<UserChallenge> userChallenges = user.getUserChallenges();
        int score = userChallenges.stream().mapToInt(UserChallenge::getScore).sum();
        log.info("User with id '{}' has score of value {}", id, score);
        return score;
    }

    public User updateUser(UUID id, UserRequestDto dto) {
        User user = findUser(id);
        user.updateUser(dto);
        User userFromDb = userRepository.save(user);
        log.info("User with id '{}' was updated", id);
        return userFromDb;
    }

    public void deleteUser(UUID id) {
        User user = findUser(id);
        userRepository.delete(user);
        log.info("User with id '{}' was deleted", id);
    }

    public void softDeleteUser(UUID id) {
        User user = findUser(id);
        obfuscatePiData(user);
        userRepository.save(user);
        log.info("User with id '{}' was soft deleted", id);
    }

    private void obfuscatePiData(User user) {
        user.setHashPassword("*".repeat(user.getHashPassword().length()));
        user.setNewsletter(false);

        if (user.getLogin() != null) {
            user.setLogin("*".repeat(user.getLogin().length()));
        }

        if (user.getName() != null) {
            user.setName("*".repeat(user.getName().length()));
        }

        if (user.getPhone() != null) {
            user.setPhone("*".repeat(user.getPhone().length()));
        }

        int charPos = user.getEmail().indexOf("@");
        String randomPrefix = "deleted-" + UUID.randomUUID();
        user.setEmail(randomPrefix + user.getEmail().substring(charPos));

        user.setDeletedAt(LocalDateTime.now());
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Attempted to get user data with id '{}', but no matching user was found in the database", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Użytkownik nie istnieje");
                });
    }
}
