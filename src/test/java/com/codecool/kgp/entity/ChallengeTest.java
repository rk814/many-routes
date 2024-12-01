package com.codecool.kgp.entity;

import com.codecool.kgp.entity.enums.Status;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.instancio.Select.field;

class ChallengeTest {

    @Test
    void addSummit_shouldAddSummitToList() {
        //given:
        Summit summit = Instancio.create(Summit.class);
        Challenge challenge = new Challenge("test", "test description", Status.ACTIVE);

        //when:
        challenge.addSummit(summit);

        //then:
        Assertions.assertThat(challenge.getSummitsSet())
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(summit);
    }

    @Test
    void removeSummit_shouldRemoveSummitFromList() {
        //given:
        Summit summit1 = Instancio.create(Summit.class);
        Summit summit2 = Instancio.create(Summit.class);
        Challenge challenge = Instancio.of(Challenge.class)
                .set(field(Challenge::getSummitsSet), Set.of(summit1, summit2))
                .create();

        //when:
        challenge.removeSummit(summit2);

        //then:
        Assertions.assertThat(challenge.getSummitsSet())
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(summit1);
    }
}