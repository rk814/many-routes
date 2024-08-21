package com.codecool.kgp.auth.registration;

import com.codecool.kgp.entity.User;
import com.codecool.kgp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public RegisterUserDataDto registerNewUser(NewUserRegistrationDto dto) {
        User user = new User(dto.username(), passwordEncoder.encode(dto.password()));
        User userFromDB = userRepository.save(user);

        return new RegisterUserDataDto(userFromDB.getId(), userFromDB.getLogin());
    }
}
