package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.*;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {
    private AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public List<AnimalDto> request(AnimalRequestIds ids) {
        List<Animal> animalList = new ArrayList<>();
        for (Long i : ids.getAnimalIds()) {
            Animal animal = animalRepository.getOne(i);
            animal.setOnsite(false);
            animalList.add(animal);
        }
        animalRepository.saveAll(animalList);
        return animalList.stream().map(AnimalMapper::mapToDto).collect(Collectors.toList());
    }

    public void returnAnimalsToShelter(List<AnimalReturnDto> animals) {
        for (AnimalReturnDto returnDto : animals) {
            Animal animal = animalRepository.getOne(returnDto.getId());
            animal.setOnsite(true);
            animal.setNotes(returnDto.getNotes());
            animalRepository.save(animal);
        }
    }

    public boolean adoptAnimals(List<Long> animalIds) {
        List<Animal> animalList = new ArrayList<>();
        for (Long id: animalIds) {
            Animal animalToUpdate = animalRepository.findById(id).get();
            if(!animalToUpdate.getStatus().equals("AVAILABLE")) {
                return false;
            }
            animalToUpdate.setStatus("ADOPTED");
            animalList.add(animalToUpdate);
        }
        animalRepository.saveAll(animalList);
        return true;
    }

    public void requestAnimalsBack(AnimalRequestIds animalRequestIds) {
        animalRequestIds.getAnimalIds().forEach(animal -> {
            Animal animalFounded = animalRepository.getOne(animal);
            animalFounded.setOnsite(true);
            animalFounded.setStatus("AVAILABLE");
            animalRepository.save(animalFounded);
        });
    }
}
