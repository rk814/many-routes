package com.codecool.kgp.controller;

import com.codecool.kgp.controller.dto.SummitDto;
import com.codecool.kgp.controller.dto.SummitSimpleDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/got/v1/summits")
public class SummitController {

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
    public SummitDto addSummit() {
        //TODO - first
        return null;
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
