package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.AnimalDto;
import com.galvanize.shelternet.model.AnimalRequestIds;
import com.galvanize.shelternet.model.AnimalReturnDto;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {
    private AnimalRepository animalRepository;

    private ModelMapper modelMapper = new ModelMapper();

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
        return animalList.stream().map(a -> modelMapper.map(a, AnimalDto.class)).collect(Collectors.toList());
    }

    public void returnAnimalsToShelter(List<AnimalReturnDto> animals) {
        for (AnimalReturnDto returnDto : animals) {
            Animal animal = animalRepository.getOne(returnDto.getAnimalId());
            animal.setOnsite(true);
            animal.setNotes(returnDto.getNotes());
            animalRepository.save(animal);
        }
    }
}
