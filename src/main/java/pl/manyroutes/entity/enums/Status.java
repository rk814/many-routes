package pl.manyroutes.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ACTIVE("active"),
    DEVELOP("develop"),
    REMOVED("removed");

    private final String name;

    public static Status fromString(String name) {
        for (Status status : Status.values()) {
            if (name.isBlank()) {
                return ACTIVE;
            }
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No such status: " + name);
    }
}
