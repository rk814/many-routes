package pl.manyroutes.service;

import pl.manyroutes.controller.dto.RegistrationRequestDto;
import pl.manyroutes.controller.dto.UserDto;
import pl.manyroutes.entity.User;
import pl.manyroutes.entity.enums.Role;
import pl.manyroutes.errorhandling.DuplicateEntryException;
import pl.manyroutes.mappers.UserMapper;
import pl.manyroutes.repository.UserRepository;
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
