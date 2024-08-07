package com.codecool.kgp.mappers;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.UserChallengeDao;
import com.codecool.kgp.controller.dto.UserSummitDto;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.entity.UserSummit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserChallengeMapper {

    private final SummitMapper summitMapper;

    public UserChallengeMapper(SummitMapper summitMapper) {

        this.summitMapper = summitMapper;
    }

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

    private List<UserSummitDto> getSummitListDto(List<UserSummit> userSummitList) {
        return userSummitList.stream().map(map -> {
                    SummitDto summitDto = summitMapper.mapEntityToDto(map.getSummit());
                    return new UserSummitDto(
                            map.getId(),
                            map.getSummit().getId(),
                            map.getUserChallenge().getId(),
                            map.getConqueredAt(),

                            summitDto.challangeList(),
                            summitDto.name(),
                            summitDto.coordinates(),
                            summitDto.mountainRange(),
                            summitDto.mountains(),
                            summitDto.height(),
                            summitDto.description(),
                            summitDto.guideNotes(),
                            summitDto.score(),
                            summitDto.status()
                    );
                }
        ).toList();
    }
}
