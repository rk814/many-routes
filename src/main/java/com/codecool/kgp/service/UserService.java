package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.User;
import com.codecool.kgp.repository.UserRepository;
import org.springframework.stereotype.Service;

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
}
