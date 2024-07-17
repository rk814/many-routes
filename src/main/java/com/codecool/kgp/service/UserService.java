package com.codecool.kgp.service;

import com.codecool.kgp.common.Coordinates;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.User;
import com.codecool.kgp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().filter(user->!user.getEmail().startsWith("deleted-")).map(userMapper::mapEntityToDto).toList();
    }

    public UserDto getUserByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje"));
        return userMapper.mapEntityToDto(user);
    }

    public UserDto setUser(UserRequestDto dto) {
        User user = userMapper.mapRequestDtoToEntity(dto);
        User userFromDb = userRepository.save(user);
        return userMapper.mapEntityToDto(userFromDb);
    }

    public UserDto updateUser(String login, UserRequestDto dto) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje"));

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setNewsletter(dto.newsletter());
        if (dto.latitude() != null && dto.longitude() != null) {
            user.setCoordinates(new Coordinates(dto.latitude(), dto.longitude()));
        } else {
            user.setCoordinates(null);
        }

        User userFromDb = userRepository.save(user);

        return userMapper.mapEntityToDto(userFromDb);
    }

    public UserDto logInUser(String login, UserRequestDto dto) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje"));
        // TODO password hash
        if (user.getHashPassword().equals(dto.password())) {
            return userMapper.mapEntityToDto(user);
        } else {
            throw new ResponseStatusException(HttpStatus.valueOf(401), "Błąd logowania");
        }
    }

    public void deleteUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje"));
        userRepository.delete(user);
    }

    public void softDeleteUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje"));
        obfuscatePiData(user);
        userRepository.save(user);
    }

    private void obfuscatePiData(User user) {
        user.setHashPassword("*".repeat(user.getHashPassword().length()));
        user.setNewsletter(false);

        if (user.getLogin()!=null) {
        user.setLogin("*".repeat(user.getLogin().length()));
        }

        if (user.getName()!=null) {
        user.setName("*".repeat(user.getName().length()));
        }

        if (user.getPhone()!=null) {
        user.setPhone("*".repeat(user.getPhone().length()));
        }

        int charPos = user.getEmail().indexOf("@");
        String randomPrefix = "deleted-" + UUID.randomUUID();
        user.setEmail(randomPrefix + user.getEmail().substring(charPos));
    }
}
