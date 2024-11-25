package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.*;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserChallengeMapper {

    public UserChallengeDto mapEntityToDto(UserChallenge userChallenge) {
        return new UserChallengeDto(
                userChallenge.getId(),
                userChallenge.getUser().getId(),

                userChallenge.getChallenge().getId(),
                userChallenge.getChallenge().getDescription(),
                userChallenge.getChallenge().getName(),
                userChallenge.getChallenge().getStatus(),

                userChallenge.getStartedAt(),
                userChallenge.getFinishedAt(),
                userChallenge.getScore(),
                getSummitListDto(userChallenge.getUserSummitsList())
        );
    }

    public UserChallengeSimpleDto mapEntityToSimpleDto(UserChallenge userChallenge) {
        return new UserChallengeSimpleDto(
                userChallenge.getId(),
                userChallenge.getUser().getId(),
                userChallenge.getChallenge().getName(),
                userChallenge.getStartedAt(),
                userChallenge.getFinishedAt(),
                userChallenge.getScore()
        );
    }

    private List<UserSummitDto> getSummitListDto(List<UserSummit> userSummitList) {
        return userSummitList.stream().map(s -> {
                    Summit summit = s.getSummit();
                    return new UserSummitDto(
                            s.getId(),
                            s.getSummit().getId(),
                            s.getUserChallenge().getId(),
                            s.getConqueredAt(),

                            summit.getChallengesList().stream().map(ch -> new ChallengeSimpleDto(
                                    ch.getId(),
                                    ch.getName(),
                                    ch.getStatus()
                            )).toList(),
                            summit.getName(),
                            summit.getCoordinatesArray(),
                            summit.getMountainRange(),
                            summit.getMountainChain(),
                            summit.getHeight(),
                            summit.getDescription(),
                            summit.getGuideNotes(),
                            summit.getScore(),
                            summit.getStatus().name()
                    );
                }
        ).toList();
    }

}
