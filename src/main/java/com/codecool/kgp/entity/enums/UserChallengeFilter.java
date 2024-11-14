package com.codecool.kgp.entity.enums;

public enum UserChallengeFilter {
    ALL("all"),
    COMPLETED("completed"),
    UNCOMPLETED("uncompleted");

    private final String name;

    UserChallengeFilter(String name) {
        this.name = name;
    }

    public static UserChallengeFilter fromString(String name) {
        for (UserChallengeFilter filter : UserChallengeFilter.values()) {
            if (name.isEmpty()) {
                return ALL;
            }
            if (filter.name.equalsIgnoreCase(name)) {
                return filter;
            }
        }
        throw new IllegalArgumentException("No such filter: " + name);
    }
}
