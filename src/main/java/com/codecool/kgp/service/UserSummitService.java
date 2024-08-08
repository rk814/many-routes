package com.codecool.kgp.service;

import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import com.codecool.kgp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UserSummitService {

    private final UserSummitRepository userSummitRepository;
    private final SummitRepository summitRepository;


    public UserSummitService(UserSummitRepository userSummitRepository, SummitRepository summitRepository
                              ) {
        this.userSummitRepository = userSummitRepository;
        this.summitRepository = summitRepository;
//        this.userChallengeService = userChallengeService;
    }

    public void setConquered(UUID id, int score) {
        UserSummit userSummit = getUserSummitById(id);
        userSummit.setConqueredAt(LocalDateTime.now());
        userSummit.setScore(score);
        userSummitRepository.save(userSummit);
        log.info("User summit with id '{}' was conquered with score value of {}", id, score);
    }

    protected UserSummit saveUserSummit(UserChallenge userChallenge, Summit summit) {
        UserSummit userSummit = new UserSummit(userChallenge, summit);
        userSummitRepository.save(userSummit);

        log.info("User summit with id '{}' and with user challenge id '{}' was saved",
                userSummit.getId(), userChallenge.getId());
        return userSummit;
    }

    private Summit getSummitById(UUID id) {
        return summitRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Summit with id {} was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Szczyt o id '%s' nie został znaleziony", id));
                });
    }

    private UserSummit getUserSummitById(UUID id) {
        return userSummitRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User summit with id {} was not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Szczyt użytkownika o id '%s' nie został znaleziony", id));
                });
    }
}
