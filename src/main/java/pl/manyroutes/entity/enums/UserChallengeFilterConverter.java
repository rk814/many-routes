package pl.manyroutes.entity.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserChallengeFilterConverter implements Converter<String, UserChallengeFilter > {

    @Override
    public UserChallengeFilter convert(@NonNull String source) {
        return UserChallengeFilter.fromString(source);
    }
}
