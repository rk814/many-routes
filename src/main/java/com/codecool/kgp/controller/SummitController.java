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
    public List<SummitSimpleDto> getAllSummitsSimplified() {
        //TODO
        return null;
    }

    @GetMapping("/{id}")
    public SummitDto getSummit(@PathVariable UUID id) {
        //TODO
        return null;
    }

    @PostMapping("/add-new")
    public SummitDto addSummit(@RequestBody @Valid SummitRequestDto dto) {
        log.info("Received request for new summit");
        return summitService.addNewSummit(dto);
    }

    @PutMapping("/{id}")
    public SummitDto updateSummit(@PathVariable UUID id) {
        //TODO
        return  null;
    }

    @DeleteMapping("/{id}")
    public void deleteSummit(@PathVariable UUID id) {
        //TODO
    }
}
