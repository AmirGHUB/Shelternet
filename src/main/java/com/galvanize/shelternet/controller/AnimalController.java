package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.AnimalDto;
import com.galvanize.shelternet.model.AnimalRequestIds;
import com.galvanize.shelternet.model.AnimalReturnDto;
import com.galvanize.shelternet.services.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    private AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public List<Animal> getAllAnimals() {
        return animalService.getAllAnimals();
    }

    @PostMapping("/request")
    public List<AnimalDto> request(@RequestBody AnimalRequestIds animalIds) {
        return animalService.request(animalIds);
    }

    @PostMapping("/return")
    public void returnAnimalsToShelter(@RequestBody List<AnimalReturnDto> animals){
        animalService.returnAnimalsToShelter(animals);
    }

    @PostMapping("/adopted")
    public ResponseEntity<Void> adoptAnimals(@RequestBody AnimalRequestIds ids) {
        return animalService.adoptAnimals(ids.getAnimalIds())
                ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
