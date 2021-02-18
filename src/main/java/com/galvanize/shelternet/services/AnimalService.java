package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {
    private AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }
}
