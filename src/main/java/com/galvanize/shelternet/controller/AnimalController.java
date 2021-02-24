package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.*;
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
    public ResponseEntity<Void> returnAnimalsToShelter(@RequestBody AnimalReturn animalReturn){
        return animalService.returnAnimalsToShelter(animalReturn.getAnimals())
                ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/adopted")
    public ResponseEntity<Void> adoptAnimals(@RequestBody AnimalRequestIds ids) {
        return animalService.adoptAnimals(ids.getAnimalIds())
                ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/return-request")
    public ResponseEntity<Void> requestAnimalsBack(@RequestBody AnimalRequestIds animalRequestIds){
         return animalService.requestAnimalsBack(animalRequestIds)
                 ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
