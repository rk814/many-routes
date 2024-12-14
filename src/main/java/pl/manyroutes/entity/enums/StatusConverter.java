package pl.manyroutes.entity.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
public class StatusConverter implements Converter<String, Status> {

    @Override
    public Status convert(@NonNull String source) {
        return Status.fromString(source);
    }
}
