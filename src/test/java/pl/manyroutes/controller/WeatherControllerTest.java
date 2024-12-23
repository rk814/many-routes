package pl.manyroutes.controller;

import pl.manyroutes.config.SpringSecurityConfig;
import pl.manyroutes.config.WithMockCustomUser;
import pl.manyroutes.repository.UserRepository;
import pl.manyroutes.service.CustomUserDetailsService;
import pl.manyroutes.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static pl.manyroutes.config.SpringSecurityConfig.ADMIN;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SpringSecurityConfig.class, CustomUserDetailsService.class})
@WebMvcTest(controllers = WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private UserRepository userRepository;


    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getCurrentWeather_shouldReturnJson() throws Exception {
        // given:
        double latitude = 12.3;
        double longitude = 45.6;

        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/current-weather")
                .param("latitude", String.valueOf(latitude))
                .param("longitude", String.valueOf(longitude)));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(weatherService).getCurrentWeather(12.3, 45.6);
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getWeatherForecast_shouldReturnJson() throws Exception {
        // given:
        double latitude = 12.3;
        double longitude = 45.6;
        int days = 3;

        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/weather-forecast")
                .param("latitude", String.valueOf(latitude))
                .param("longitude", String.valueOf(longitude))
                .param("days", String.valueOf(days)));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(weatherService).getWeatherForecast(12.3, 45.6, 3);
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getWeatherForecast_shouldReturn400_whenDaysParameterIsNotInValidRange() throws Exception {
        // given:
        double latitude = 12.3;
        double longitude = 45.6;
        int days = 8; // max 7

        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/weather-forecast")
                .param("latitude", String.valueOf(latitude))
                .param("longitude", String.valueOf(longitude))
                .param("days", String.valueOf(days)));

        //then:
        response.andExpect(status().isBadRequest());
        Mockito.verify(weatherService, Mockito.times(0))
                .getWeatherForecast(any(Double.class),any(Double.class), any(Integer.class));
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getCurrentAstronomy_shouldReturnJson() throws Exception {
        // given:
        double latitude = 12.3;
        double longitude = 45.6;


        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/current-astronomy")
                .param("latitude", String.valueOf(latitude))
                .param("longitude", String.valueOf(longitude)));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(weatherService).getCurrentAstronomy(12.3, 45.6);
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getCurrentAstronomy_shouldReturn400_whenParameterTypeDoseNotMatch() throws Exception {
        // given:
        String latitude = "xx.x";
        double longitude = 45.6;


        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/current-astronomy")
                .param("latitude", latitude)
                .param("longitude", String.valueOf(longitude)));

        //then:
        response.andExpect(status().isBadRequest());
        Mockito.verify(weatherService, Mockito.times(0)).getCurrentAstronomy(any(Double.class), any(Double.class));
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getCurrentAstronomy_shouldReturn400_whenRequiredParameterIsObsolete() throws Exception {
        // given:
        double longitude = 45.6;


        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/current-astronomy")
                .param("longitude", String.valueOf(longitude)));

        //then:
        response.andExpect(status().isBadRequest());
        Mockito.verify(weatherService, Mockito.times(0)).getCurrentAstronomy(any(Double.class), any(Double.class));
    }
}