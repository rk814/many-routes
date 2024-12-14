package pl.manyroutes.entity.enums;

import org.springframework.lang.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChallengeFilterConverter implements Converter<String, ChallengeFilter> {

    @Override
    public ChallengeFilter convert(@NonNull String source) {
        return ChallengeFilter.fromString(source);
    }
}
