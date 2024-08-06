package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.UserChallengeDao;
import com.codecool.kgp.repository.UserChallenge;
import com.codecool.kgp.repository.UserSummit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserChallengeMapper {
    public UserChallengeDao mapEntityToDto(UserChallenge userChallenge) {
        return new UserChallengeDao(
                userChallenge.getId(),
                userChallenge.getUser().getId(),
                new UserChallengeDao.ChallengeDto(userChallenge.getChallenge().getId(),
                        userChallenge.getChallenge().getName()),
                userChallenge.getStartedAt(),
                userChallenge.getFinishedAt(),
                userChallenge.getScore(),
                getSummitListDto(userChallenge.getUserSummitList())
        );
    }

    private List<UserChallengeDao.UserSummitDto> getSummitListDto(List<UserSummit> userSummitList) {
        return userSummitList.stream().map(map -> new UserChallengeDao.UserSummitDto(
                map.getId(),
                map.getConqueredAt()
        )).toList();
    }
}
