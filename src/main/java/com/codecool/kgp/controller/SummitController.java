package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.service.SummitService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;

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
    @RolesAllowed(ADMIN)
    public List<SummitSimpleDto> getSummitsSimplified() {
        log.info("Received request for all simplify summits");
        return summitService.getAllSummitsSimplified();
    }

    //TODO
    @GetMapping("/{id}")
    @RolesAllowed(ADMIN)
    public SummitDto getSummit(@PathVariable UUID id) {
        throw new ResponseStatusException(HttpStatusCode.valueOf(501), "Not implemented yet");
    }

    @PostMapping("/add-new")
    @RolesAllowed(ADMIN)
    public SummitDto addSummit(@RequestBody @Valid SummitRequestDto dto) {
        log.info("Received request for new summit");
        return summitService.addNewSummit(dto);
    }

    //TODO
    @PutMapping("/{id}")
    @RolesAllowed(ADMIN)
    public SummitDto updateSummit(@PathVariable UUID id) {
        throw new ResponseStatusException(HttpStatusCode.valueOf(501), "Not implemented yet");
    }

    //TODO
    @DeleteMapping("/{id}")
    @RolesAllowed(ADMIN)
    public void deleteSummit(@PathVariable UUID id) {
        throw new ResponseStatusException(HttpStatusCode.valueOf(501), "Not implemented yet");
    }
}
