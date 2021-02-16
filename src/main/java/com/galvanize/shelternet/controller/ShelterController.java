package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.services.ShelternetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShelterController {

    private ShelternetService shelternetService;

    public ShelterController(ShelternetService shelternetService) {
        this.shelternetService = shelternetService;
    }

    @GetMapping("/home")
    public String home() {
        return "Welcome to Shelternet";
    }

    @PostMapping("/shelter")
    @ResponseStatus(HttpStatus.CREATED)
    public Shelter registerShelter(@RequestBody Shelter shelter) {
        return shelternetService.registerShelter(shelter);
    }
}
