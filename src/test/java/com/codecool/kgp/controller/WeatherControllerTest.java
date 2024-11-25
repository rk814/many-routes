package com.codecool.kgp.controller;

import com.codecool.kgp.config.SpringSecurityConfig;
import com.codecool.kgp.config.WithMockCustomUser;
import com.codecool.kgp.repository.UserRepository;
import com.codecool.kgp.service.CustomUserDetailsService;
import com.codecool.kgp.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
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
        ResultActions response = mockMvc.perform(get("/api/v1/weather/get-current-weather")
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
        ResultActions response = mockMvc.perform(get("/api/v1/weather/get-weather-forecast")
                .param("latitude", String.valueOf(latitude))
                .param("longitude", String.valueOf(longitude))
                .param("days", String.valueOf(days)));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(weatherService).getWeatherForecast(12.3, 45.6, 3);
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getCurrentAstronomy_shouldReturnJson() throws Exception {
        // given:
        double latitude = 12.3;
        double longitude = 45.6;


        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/get-current-astronomy")
                .param("latitude", String.valueOf(latitude))
                .param("longitude", String.valueOf(longitude)));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(weatherService).getCurrentAstronomy(12.3, 45.6);
    }

    @Test
    @WithMockCustomUser(username = "adam_wanderlust", role = ADMIN, id = "5c39c496-ff63-4c8a-bad4-47d6a97053e7")
    void getCurrentAstronomy_shouldReturn400() throws Exception {
        // given:
        String latitude = "xx.x";
        double longitude = 45.6;


        // when:
        ResultActions response = mockMvc.perform(get("/api/v1/weather/get-current-astronomy")
                .param("latitude", latitude)
                .param("longitude", String.valueOf(longitude)));

        //then:
        response.andExpect(status().isBadRequest());
        Mockito.verify(weatherService, Mockito.times(0)).getCurrentAstronomy(any(Double.class), any(Double.class));
    }
}