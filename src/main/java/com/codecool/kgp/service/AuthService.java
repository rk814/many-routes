package com.codecool.kgp.service;

import com.codecool.kgp.controller.dto.RegistrationRequestDto;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.enums.Role;
import com.codecool.kgp.errorhandling.DuplicateEntryException;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto registerNewUser(RegistrationRequestDto dto) {
        return register(dto, Role.USER);
    }

    public void registerNewAdmin(RegistrationRequestDto dto) {
        register(dto, Role.ADMIN);
    }


    private UserDto register(RegistrationRequestDto dto, Role role) {
        User user = new User(
                dto.login(),
                passwordEncoder.encode(dto.password()),
                dto.email(),
                role
        );

        try {
            User savedUser = userRepository.save(user);
            log.info("New user with id '{}' was added to database", user.getId());
            return userMapper.mapEntityToDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            log.warn("Data integrity violation when adding user with login '{}' to database", user.getLogin());
            throw new DuplicateEntryException("Podany login lub e-mail istnieją już w naszym serwisie");
        }

    }
}
