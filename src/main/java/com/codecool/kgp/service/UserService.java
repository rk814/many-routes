package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.User;
import com.codecool.kgp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::mapEntityToDto).toList();
    }

    public UserDto getUserByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Login nie istnieje"));
        return userMapper.mapEntityToDto(user);
    }

    public UserDto setUser(UserRequestDto dto) {
        User user = userMapper.mapRequestDtoToEntity(dto);
        User userFromDb = userRepository.save(user);
        return userMapper.mapEntityToDto(userFromDb);
    }
}
