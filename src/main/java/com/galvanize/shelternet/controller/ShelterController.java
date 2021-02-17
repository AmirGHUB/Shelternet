package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.services.ShelternetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/shelter")
    public List<Shelter> getAllShelters(){
        return shelternetService.getAllShelters();
    }

    @GetMapping("/shelter/{id}")
    public Optional<Shelter> getShelterDetails(@PathVariable Long id) {
        return shelternetService.getShelterDetails(id);
    }

    @PutMapping("/shelter/{id}")
    public Shelter updateShelter(@PathVariable Long id, @RequestBody Shelter shelterToUpdate) {
        return shelternetService.updateShelter(id, shelterToUpdate);
    }

    @DeleteMapping("/shelter/{id}")
    public void deleteShelter(@PathVariable Long id) {
        shelternetService.delete(id);
    }
}
