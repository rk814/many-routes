package com.codecool.kgp.service;

import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.User;
import com.codecool.kgp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> !user.getEmail().startsWith("deleted-"))
                .map(userMapper::mapEntityToDto).toList();
    }

    public UserDto getUserByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Attempted to get user data with login '{}', but no matching user was found in the database", login);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje");
                });
        log.info("User with id '{}' got user data", user.getId());
        return userMapper.mapEntityToDto(user);
    }

    public UserDto setUser(UserRequestDto dto) {
        User user = userMapper.mapRequestDtoToEntity(dto);
        try {
            User userFromDb = userRepository.saveAndFlush(user);
            log.info("New user with id '{}' was added to database", user.getId());
            return userMapper.mapEntityToDto(userFromDb);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation when adding user with login '{}' to database", user.getLogin());
            throw new DuplicateEntryException("Podany login lub e-mail istnieją już w naszym serwisie");
        }
    }

    public UserDto updateUser(String login, UserRequestDto dto) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Attempted to update user with login '{}', but no matching user was found in the database", login);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje");
                });

        user.updateUser(dto);

        User userFromDb = userRepository.save(user);
        log.info("User with id '{}' was updated",userFromDb.getId());
        return userMapper.mapEntityToDto(userFromDb);
    }

    public UserDto logInUser(String login, UserRequestDto dto) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.info("User attempted to sign in, but provided non existing login '{}'", login);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje");
                });
        // TODO password hash
        if (user.getHashPassword().equals(dto.password())) {
            log.info("User with id '{}' sign in", user.getId());
            return userMapper.mapEntityToDto(user);
        } else {
            log.info("User with id '{}' typed wrong password", user.getId());
            throw new ResponseStatusException(HttpStatus.valueOf(401), "Błąd logowania");
        }
    }

    public void deleteUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Attempted to delete user with login '{}', but no matching user was found in the database", login);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje");
                });
        userRepository.delete(user);
        log.info("User with id '{}' was deleted", user.getId());
    }

    public void softDeleteUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Attempted to soft delete user with login '{}', but no matching user was found in the database", login);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje");
                });
        obfuscatePiData(user);
        userRepository.save(user);
        log.info("User with id '{}' was soft deleted", user.getId());
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
}
