package com.codecool.kgp.service;

import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.repository.SummitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Transactional
@Service
public class SummitService {

    public final SummitRepository summitRepository;

    public SummitService(SummitRepository summitRepository) {
        this.summitRepository = summitRepository;
    }


    public Summit getSummit(UUID summitId) {
        Summit summit = findSummit(summitId);
        log.info("Summit with id '{}' was found", summitId);
        return summit;
    }

    public List<Summit> getAllSummits(Status status) {
        List<Summit> summits = summitRepository.findAllByStatus(status);
        log.info("{} summitsList were found", summits.size());
        return summits;
    }

    public Summit addNewSummit(Summit summit) {
        Summit summitFromDb = summitRepository.save(summit);
        log.info("New summit with id '{}' was saved", summit.getId());
        return summitFromDb;
    }

    public Summit updateSummit(UUID summitId, Summit summit) {
        Summit summitFromDb = findSummit(summitId);
        summitFromDb.updateSummit(summit);
        summitRepository.save(summitFromDb);
        log.info("Summit with id '{}' was successfully updated", summitId);
        return summitFromDb;
    }

    public void deleteSummit(UUID summitId) {
        Summit summitFromDb = findSummit(summitId);
        summitRepository.delete(summitFromDb);
        log.info("Summit with id '{}' was successfully deleted", summitId);
    }

    private Summit findSummit(UUID summitId) {
        return summitRepository.findById(summitId).orElseThrow(() -> {
            log.warn("Summit with id '{}' was not found", summitId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Summit was not found");
        });
    }
}
