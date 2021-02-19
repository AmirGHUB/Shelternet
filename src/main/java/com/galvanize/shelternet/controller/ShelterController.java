package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.*;
import com.galvanize.shelternet.services.ShelternetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/shelters")
public class ShelterController {


    private ShelternetService shelternetService;

    public ShelterController(ShelternetService shelternetService) {
        this.shelternetService = shelternetService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterDto registerShelter(@RequestBody Shelter shelter) {
        return shelternetService.registerShelter(shelter);
    }

    @GetMapping
    public List<ShelterDto> getAllShelters() {
        return shelternetService.getAllShelters();
    }

    @GetMapping("/{id}")
    public ShelterDto getShelterDetails(@PathVariable Long id) {
        return shelternetService.getShelterDetails(id);
    }

    @PostMapping("/{id}/animal")
    public AnimalDto surrenderAnimal(@PathVariable Long id, @RequestBody AnimalDto animal) {
        return shelternetService.surrenderAnimal(id, animal);
    }

    @PutMapping("/{id}")
    public ShelterDto updateShelter(@PathVariable Long id, @RequestBody Shelter shelterToUpdate) {
        return shelternetService.updateShelter(id, shelterToUpdate);
    }

    @PutMapping("/transfer-animal")
    public ResponseEntity<Void> transferAnimal(@RequestBody AnimalTransfer animalTransfer) {
        return shelternetService.transferAnimal(animalTransfer)
                ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public void deleteShelter(@PathVariable Long id) {
        shelternetService.delete(id);
    }

    @GetMapping("/{shelterId}/animals")
    public List<Animal> getAnimalsByShelterId(@PathVariable Long shelterId) {
        return shelternetService.getAnimalsByShelterId(shelterId);
    }
}
