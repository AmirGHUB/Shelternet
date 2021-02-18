package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.services.AnimalService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    private AnimalService animalService;
    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public List<Animal> getAllAnimals(){
        return animalService.getAllAnimals();
    }
}
