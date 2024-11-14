package com.codecool.kgp.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChallengeFilter {
    ALL("all"),
    UNSTARTED("unstarted");

    private final String name;

    public static ChallengeFilter fromString(String name) {
        for (ChallengeFilter filter : ChallengeFilter.values()) {
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
