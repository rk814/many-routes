package com.codecool.kgp.service;

import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import com.codecool.kgp.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserSummitService {

    private final UserSummitRepository userSummitRepository;
    private final SummitRepository summitRepository;
    private final UserChallengeService userChallengeService;

    public UserSummitService(UserSummitRepository userSummitRepository, SummitRepository summitRepository,
                             UserChallengeService userChallengeService) {
        this.userSummitRepository = userSummitRepository;
        this.summitRepository = summitRepository;
        this.userChallengeService = userChallengeService;
    }

    public void setConquered(UUID id, int score) {
        UserSummit userSummit = getUserSummitById(id);
        userSummit.setConqueredAt(LocalDateTime.now());
        userSummit.setScore(score);
        userSummitRepository.save(userSummit);
    }

    public void saveUserSummit(UUID userChallengeId, UUID summitId) {
        Summit summit = getSummitById(summitId);
        UserChallenge userChallenge = userChallengeService.getUserChallengeById(userChallengeId);

        saveUserSummit(userChallenge, summit);
    }

    public void saveUserSummit(UserChallenge userChallenge, Summit summit) {
        UserSummit userSummit = new UserSummit(userChallenge, summit);
        userSummitRepository.save(userSummit);
        userChallenge.assignUserSummit(userSummit);
    }

    private Summit getSummitById(UUID id) {
        return summitRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Szczyt o id '%s' nie istnieje", id)));
    }

    private UserSummit getUserSummitById(UUID id) {
        return userSummitRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Szczyt użytkownika o id '%s' nie istnieje", id)));
    }
}
