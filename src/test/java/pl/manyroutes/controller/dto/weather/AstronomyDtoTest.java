package pl.manyroutes.controller.dto.weather;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AstronomyDtoTest {


    @ParameterizedTest
    @CsvSource(value = {
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, New Moon, Nów, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, Waxing Crescent, Rosnący Sierp, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, First Quarter,Pierwsza Kwadra, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, Waxing Gibbous, Rosnący Garb, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, Full Moon, Pełnia, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, Waning Gibbous, Znikający Garb, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, Third Quarter, Trzecia Kwadra, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, Waning Crescent, Znikający Sierp, 75",
            "5:00 AM, 6:00 PM, 6:30 AM, 7:00 PM, Moon, Moon, 75"
    })
    void astronomyDto_createAstronomyDto(
            String sunrise, String sunset, String moonrise, String moonset, String phase, String phaseTranslation, int moon_illumination) {

        //when:
        AstronomyDto actual = new AstronomyDto(sunrise, sunset, moonrise, moonset, phase, moon_illumination);

        //then:
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).extracting(AstronomyDto::sunrise).isEqualTo(sunrise);
        Assertions.assertThat(actual).extracting(AstronomyDto::sunset).isEqualTo(sunset);
        Assertions.assertThat(actual).extracting(AstronomyDto::moonrise).isEqualTo(moonrise);
        Assertions.assertThat(actual).extracting(AstronomyDto::moonset).isEqualTo(moonset);
        Assertions.assertThat(actual).extracting(AstronomyDto::phase).isEqualTo(phaseTranslation);
        Assertions.assertThat(actual).extracting(AstronomyDto::moon_illumination).isEqualTo(moon_illumination);
    }


}