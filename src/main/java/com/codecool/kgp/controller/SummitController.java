package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.service.SummitService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/summits")
public class SummitController {

    private final SummitService summitService;

    public SummitController(SummitService summitService) {
        this.summitService = summitService;
    }


    @GetMapping("/")
    public List<SummitSimpleDto> getSummitsSimplified() {
        log.info("Received request for all simplify summits");
        return summitService.getAllSummitsSimplified();
    }

    //TODO
    @GetMapping("/{id}")
    public SummitDto getSummit(@PathVariable UUID id) {
        return null;
    }

    @PostMapping("/add-new")
    public SummitDto addSummit(@RequestBody @Valid SummitRequestDto dto) {
        log.info("Received request for new summit");
        // TODO check name uniques
        return summitService.addNewSummit(dto);
    }

    //TODO
    @PutMapping("/{id}")
    public SummitDto updateSummit(@PathVariable UUID id) {
        return null;
    }

    //TODO
    @DeleteMapping("/{id}")
    public void deleteSummit(@PathVariable UUID id) {
    }
}
