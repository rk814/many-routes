package com.codecool.kgp.controller;

import com.codecool.kgp.config.swagger.ApiCreateResponses;
import com.codecool.kgp.config.swagger.ApiGeneralResponses;
import com.codecool.kgp.config.swagger.ApiRetrieveUpdateDeleteResponses;
import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitRequestDto;
import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import com.codecool.kgp.mappers.SummitMapper;
import com.codecool.kgp.service.SummitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static com.codecool.kgp.config.SpringSecurityConfig.USER;


@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/summits")
public class SummitController {

    private final SummitService summitService;

    private final SummitMapper summitMapper;

    public SummitController(SummitService summitService, SummitMapper summitMapper) {
        this.summitService = summitService;
        this.summitMapper = summitMapper;
    }

    @GetMapping("/")
    @Operation(summary = "Retrieve list of summits (default ACTIVE)")
    @ApiGeneralResponses
    @RolesAllowed(ADMIN)
    public List<SummitDto> getSummits(
            @Parameter(description = "Challenge status")
            @RequestParam(required = false, defaultValue = "ACTIVE") Status status,
            @Parameter(description = "A comma-separated list of field names to customize the fields returned in the SummitDto response\", example = \"challengeName,id")
            @RequestParam(required = false) List<String> fields
    ) {
        log.info("Received request for all summitsSet");
        List<Summit> summits = summitService.getAllSummits(status);
        return summits.stream().map(summit -> summitMapper.mapEntityToDto(summit, fields)).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve summit")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed({ADMIN, USER})
    public SummitDto getSummit(@PathVariable UUID id) {
        log.info("Received request for summit with id '{}'", id);
        Summit summit = summitService.getSummit(id);
        return summitMapper.mapEntityToDto(summit);
    }

    @PostMapping("/add-new")
    @Operation(summary = "Create new summit")
    @ApiCreateResponses
    @RolesAllowed(ADMIN)
    public SummitDto addSummit(@RequestBody @Valid SummitRequestDto dto) {
        log.info("Received request for new summit");
        Summit summit = summitMapper.mapRequestDtoToEntity(dto);
        Summit summitFormDB = summitService.addNewSummit(summit);
        return summitMapper.mapEntityToDto(summitFormDB);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update summit")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed(ADMIN)
    public SummitDto updateSummit(@PathVariable UUID id, @RequestBody @Valid SummitRequestDto dto) {
        log.info("Received request for summit update with id '{}'", id);
        Summit summit = summitMapper.mapRequestDtoToEntity(dto);
        Summit updatedSummit = summitService.updateSummit(id, summit);
        return summitMapper.mapEntityToDto(updatedSummit);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete summit")
    @ApiRetrieveUpdateDeleteResponses
    @RolesAllowed(ADMIN)
    public void deleteSummit(@PathVariable UUID id) {
        log.info("Received request for summit delete with id '{}'", id);
        summitService.deleteSummit(id);
    }
}
