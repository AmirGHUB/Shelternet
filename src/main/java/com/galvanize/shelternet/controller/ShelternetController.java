package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.services.ShelternetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShelternetController {

    private ShelternetService shelternetService;

    public ShelternetController(ShelternetService shelternetService) {
        this.shelternetService = shelternetService;
    }

    @GetMapping("/home")
    public String home() {
        return "Welcome to Shelternet";
    }

    @PostMapping("/shelter/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Shelter registerShelter(@RequestBody Shelter shelter, @PathVariable Long id) {
        return shelternetService.registerShelter(shelter, id);
    }
}
