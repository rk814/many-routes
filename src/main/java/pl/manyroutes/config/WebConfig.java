package pl.manyroutes.config;

import pl.manyroutes.entity.enums.ChallengeFilterConverter;
import pl.manyroutes.entity.enums.StatusConverter;
import pl.manyroutes.entity.enums.UserChallengeFilterConverter;
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
