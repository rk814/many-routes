package pl.manyroutes.controller;


import pl.manyroutes.config.swagger.ApiGeneralResponses;
import pl.manyroutes.controller.dto.weather.AstronomyDto;
import pl.manyroutes.auth.CustomUserDetails;
import pl.manyroutes.service.WeatherService;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static pl.manyroutes.config.SpringSecurityConfig.ADMIN;
import static pl.manyroutes.config.SpringSecurityConfig.USER;


@Slf4j
@RestController
@RequestMapping("api/v1/weather")
@CrossOrigin(origins = "http://localhost:3000")
public class WeatherController {

    private final WeatherService weatherService;


    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


    @GetMapping(value = "/current-weather", produces = "application/json")
    @Operation(summary = "Retrieve current weather on the given location")
    @ApiGeneralResponses
    @ApiResponses(value = @ApiResponse(responseCode = "400", description = "A required parameter is either missing or has an incorrect type"))
    @RolesAllowed({USER, ADMIN})
    public JsonObject getCurrentWeather(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request for current weather for location [{},{}] from the user with id '{}'", latitude, longitude, userId);

        return weatherService.getCurrentWeather(latitude, longitude);
    }

    @GetMapping(value = "/weather-forecast", produces = "application/json")
    @Operation(summary = "Retrieve weather forecast on the given location")
    @ApiGeneralResponses
    @ApiResponses(value = @ApiResponse(responseCode = "400", description = "A required parameter is either missing or has an incorrect type"))
    @RolesAllowed({USER, ADMIN})
    public JsonObject getForecast(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @Parameter(description = "Number of days for the forecast. Maximum 7 days.")
            @RequestParam @Min(value = 1, message = "Min value is 1") @Max(value = 7, message = "Max value is 7") int days
    ) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request for weather forecast for location [{},{}], for next {} days from the user with id '{}'", latitude, longitude, days, userId);
        return weatherService.getWeatherForecast(latitude, longitude, days);
    }

    @GetMapping(value = "/current-astronomy", produces = "application/json")
    @Operation(summary = "Retrieve current astronomy on the given location")
    @ApiGeneralResponses
    @ApiResponses(value = @ApiResponse(responseCode = "400", description = "A required parameter is either missing or has an incorrect type"))
    @RolesAllowed({USER, ADMIN})
    public AstronomyDto getAstronomy(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        CustomUserDetails cud = (CustomUserDetails) userDetails;
        UUID userId = cud.getUserId();
        log.info("Received request for current astronomy for location [{},{}] from the user with id '{}'", latitude, longitude, userId);
        return weatherService.getCurrentAstronomy(latitude, longitude);
    }
}
