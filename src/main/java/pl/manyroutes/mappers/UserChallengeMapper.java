package pl.manyroutes.mappers;

import pl.manyroutes.entity.Summit;
import pl.manyroutes.entity.UserChallenge;
import pl.manyroutes.entity.UserSummit;
import org.springframework.stereotype.Component;
import pl.manyroutes.controller.dto.ChallengeSimpleDto;
import pl.manyroutes.controller.dto.UserChallengeDto;
import pl.manyroutes.controller.dto.UserChallengeSimpleDto;
import pl.manyroutes.controller.dto.UserSummitDto;

import java.util.Set;
import java.util.stream.Collectors;

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
                getSummitSetDto(userChallenge.getUserSummitsSet())
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

    private Set<UserSummitDto> getSummitSetDto(Set<UserSummit> userSummitSet) {
        return userSummitSet.stream().map(s -> {
                    Summit summit = s.getSummit();
                    return new UserSummitDto(
                            s.getId(),
                            s.getSummit().getId(),
                            s.getUserChallenge().getId(),
                            s.getConqueredAt(),

                            summit.getChallengesSet().stream().map(ch -> new ChallengeSimpleDto(
                                    ch.getId(),
                                    ch.getName(),
                                    ch.getStatus()
                            )).collect(Collectors.toSet()),
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
        ).collect(Collectors.toSet());
    }

}
