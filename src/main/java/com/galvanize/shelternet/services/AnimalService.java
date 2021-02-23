package com.galvanize.shelternet.services;

import com.galvanize.shelternet.client.PetStoreClient;
import com.galvanize.shelternet.model.*;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private AnimalRepository animalRepository;
    private PetStoreClient petStoreClient;

    public AnimalService(AnimalRepository animalRepository, PetStoreClient petStoreClient) {
        this.animalRepository = animalRepository;
        this.petStoreClient = petStoreClient;
    }

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public List<AnimalDto> request(AnimalRequestIds ids) {
        List<Animal> animalList = new ArrayList<>();
        for (Long i : ids.getAnimalIds()) {
            Animal animal = animalRepository.getOne(i);
            animal.setStatus("OFFSITE");
            animalList.add(animal);
        }
        animalRepository.saveAll(animalList);
        return animalList.stream().map(AnimalMapper::mapToDto).collect(Collectors.toList());
    }

    public boolean returnAnimalsToShelter(List<AnimalReturnDto> animals) {
        List<Animal> animalList = new ArrayList<>();
        for (AnimalReturnDto returnDto : animals) {
            Animal animal = animalRepository.getOne(returnDto.getId());
            if (!isAnimalOffsite(animal)) {
                return false;
            }
            animal.setStatus("AVAILABLE");
            animal.setNotes(returnDto.getNotes());
            animalList.add(animal);
        }
        animalRepository.saveAll(animalList);
        return true;
    }

    public boolean adoptAnimals(List<Long> animalIds) {
        List<Animal> animalList = new ArrayList<>();
        for (Long id : animalIds) {
            Animal animalToUpdate = animalRepository.findById(id).get();
            if (!isAnimalOffsite(animalToUpdate)) {
                return false;
            }
            animalToUpdate.setStatus("ADOPTED");
            animalList.add(animalToUpdate);
        }
        animalRepository.saveAll(animalList);
        return true;
    }

    private boolean isAnimalOffsite(Animal animal) {
        return animal.getStatus().equals("OFFSITE");
    }

    public void requestAnimalsBack(AnimalRequestIds animalRequestIds) {
        List<AnimalReturnFromPetStoreDto> animalReturnFromPetStoreDtoList = petStoreClient.returnRequest(animalRequestIds);

        animalReturnFromPetStoreDtoList.forEach(animal -> {
            Animal animalFounded = animalRepository.getOne(animal.getId());
            animalFounded.setStatus("AVAILABLE");
            animalFounded.setNotes(animal.getNote());
            animalRepository.save(animalFounded);
        });
    }
}
