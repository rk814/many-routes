package com.codecool.kgp.service;

import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.repository.SummitRepository;
import org.instancio.Instancio;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;


class SummitServiceTest {

    private final SummitRepository summitRepository = Mockito.mock();
    private final SummitService summitService = new SummitService(summitRepository);


    @Test
    void getSummit_shouldReturnSummit() {
        //given:
        Summit summit = Instancio.create(Summit.class);
        Mockito.when(summitRepository.findById(summit.getId())).thenReturn(Optional.of(summit));

        //when:
        Summit actual = summitService.getSummit(summit.getId());

        //then:
        Assertions.assertThat(actual).isEqualTo(summit);
    }

    @Test
    void getSummit_shouldThrow404() {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(summitRepository.findById(id)).thenReturn(Optional.empty());

        //when & then:
        Assertions.assertThatThrownBy(() -> summitService.getSummit(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found")
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllSummits_shouldReturnAllSummits() {
        //given:
        Status status = Status.ACTIVE;
        List<Summit> summitList = Instancio.ofList(Summit.class).size(4)
                .set(field(Summit::getStatus), status).create();

        Mockito.when(summitRepository.findAllByStatusWithChallenges(status)).thenReturn(summitList);

        //when:
        List<Summit> actual = summitService.getAllSummits(status);

        //then:
        Assertions.assertThat(actual)
                .hasSize(4)
                .allMatch(e -> e.getStatus().equals(status));
    }

    @Test
    void addNewSummit_shouldReturnNewSummit() {
        //given:
        Summit summit = Instancio.create(Summit.class);

        Mockito.when(summitRepository.save(summit)).thenReturn(summit);

        //when:
        Summit actual = summitService.addNewSummit(summit);

        //then:
        Assertions.assertThat(actual).isEqualTo(summit);
        Mockito.verify(summitRepository).save(summit);
    }

    @Test
    void updateSummit_shouldReturnUpdatedSummit() {
        //given:
        Summit oldSummit = Instancio.create(Summit.class);
        Summit newSummit = Instancio.create(Summit.class);

        Mockito.when(summitRepository.findById(oldSummit.getId())).thenReturn(Optional.of(oldSummit));

        //when:
        Summit actual = summitService.updateSummit(oldSummit.getId(), newSummit);

        //then:
        Assertions.assertThat(actual).isEqualTo(newSummit);
        Mockito.verify(summitRepository).save(newSummit);
    }

    @Test
    void updateSummit_shouldThrow404() {
        //given:
        UUID id = UUID.randomUUID();
        Summit newSummit = Instancio.create(Summit.class);

        Mockito.when(summitRepository.findById(id)).thenReturn(Optional.empty());

        //when & then:
        Assertions.assertThatThrownBy(() -> summitService.updateSummit(id, newSummit))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found")
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteSummit_shouldDeleteSummit() {
        //given:
        Summit summit = Instancio.create(Summit.class);
        Mockito.when(summitRepository.findById(summit.getId())).thenReturn(Optional.of(summit));

        //when:
        summitService.deleteSummit(summit.getId());

        //then:
        Mockito.verify(summitRepository).delete(summit);
    }

    @Test
    void deleteSummit_shouldThrow404() {
        //given:
        UUID id = UUID.randomUUID();
        Mockito.when(summitRepository.findById(id)).thenReturn(Optional.empty());

        //when & then:
        Assertions.assertThatThrownBy(()->summitService.deleteSummit(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found")
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }
}