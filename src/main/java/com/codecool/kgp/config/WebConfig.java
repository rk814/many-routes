package com.codecool.kgp.config;

import com.codecool.kgp.entity.enums.ChallengeFilterConverter;
import com.codecool.kgp.entity.enums.StatusConverter;
import com.codecool.kgp.entity.enums.UserChallengeFilterConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ChallengeFilterConverter());
        registry.addConverter(new UserChallengeFilterConverter());
        registry.addConverter(new StatusConverter());
    }
}
