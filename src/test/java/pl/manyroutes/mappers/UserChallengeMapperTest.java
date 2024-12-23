package pl.manyroutes.mappers;

import pl.manyroutes.controller.dto.UserChallengeDto;
import pl.manyroutes.controller.dto.UserChallengeSimpleDto;
import pl.manyroutes.controller.dto.UserSummitDto;
import pl.manyroutes.entity.UserChallenge;
import pl.manyroutes.entity.UserSummit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.instancio.Select.field;

class UserChallengeMapperTest {

    private final UserChallengeMapper mapper = new UserChallengeMapper();

    @Test
    void mapEntityToDto_shouldMapEntityToDto() {
        //given:
        UserChallenge userChallenge = Instancio.of(UserChallenge.class)
                .ignore(field(UserChallenge::getUserSummitsSet))
                .create();

        List<UserSummit> userSummits = Instancio.ofList(UserSummit.class)
                .size(3)
                .set(field(UserSummit::getUserChallenge), userChallenge)
                .create();

        userSummits.forEach(userChallenge::assignUserSummit);


        //when:
        UserChallengeDto actual = mapper.mapEntityToDto(userChallenge);

        //then:
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).extracting(UserChallengeDto::id).isEqualTo(userChallenge.getId());
        Assertions.assertThat(actual).extracting(UserChallengeDto::userSummitsSet, InstanceOfAssertFactories.iterable(UserSummitDto.class))
                .size().isEqualTo(3);
    }

    @Test
    void mapEntityToSimpleDto_shouldMapEntityToDto() {
        //given:
        UserChallenge userChallenge = Instancio.create(UserChallenge.class);

        //when:
        UserChallengeSimpleDto actual = mapper.mapEntityToSimpleDto(userChallenge);

        //then:
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).extracting(UserChallengeSimpleDto::id).isEqualTo(userChallenge.getId());
    }
}