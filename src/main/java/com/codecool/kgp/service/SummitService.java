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


    public Summit getSummit(UUID id) {
        Summit summit = findSummit(id);
        log.info("Summit with id '{}' was found", id);
        return summit;
    }

    public List<Summit> getAllSummits(Status status) {
        List<Summit> summits = summitRepository.findAllByStatus(status);
        log.info("{} summitList were found", summits.size());
        return summits;
    }

//    public List<SummitSimpleDto> getAllSummitsSimplified() {
//        List<Summit> summits = summitRepository.findAll();
//        log.info("{} summitList were found", summits.size());
//        return summits.stream().map(summitMapper::mapEntityToSimpleDto).toList();
//    }

    public Summit addNewSummit(Summit summit) {
        Summit summitFromDb = summitRepository.save(summit);
        log.info("New summit with id '{}' was saved", summit.getId());
        return summitFromDb;
    }

    public Summit updateSummit(UUID id, Summit summit) {
        Summit summitFromDb = findSummit(id);
        summitFromDb.updateSummit(summit);
        summitRepository.save(summitFromDb);
        log.info("Summit with id '{}' was successfully updated", id);
        return summitFromDb;
    }

    public void deleteSummit(UUID id) {
        Summit summitFromDb = findSummit(id);
        summitRepository.delete(summitFromDb);
        log.info("Summit with id '{}' was successfully deleted", id);
    }

    private Summit findSummit(UUID id) {
        return summitRepository.findById(id).orElseThrow(() -> {
            log.warn("Challenge with id '{}' was not found", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Summit was not found");
        });
    }
}
