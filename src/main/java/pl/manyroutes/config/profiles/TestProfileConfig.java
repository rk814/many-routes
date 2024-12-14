package pl.manyroutes.config.profiles;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestProfileConfig {
    @Bean
    public ApplicationRunner initializer() {
        return args -> {
        };
    }
}
