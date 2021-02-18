package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShelternetService {

    private ShelterRepository shelterRepository;

    public ShelternetService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public Shelter registerShelter(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    public Optional<Shelter> getShelterDetails(Long id) {
        return shelterRepository.findById(id);
    }

    public Animal surrenderAnimal(Long id, Animal animal) {
        Shelter shelter = shelterRepository.getOne(id);
        shelter.addAnimal(animal);
        shelter.setCapacity(shelter.getCapacity()-1);
        Shelter shelter1 = shelterRepository.save(shelter);
        return shelter1.getAnimals().stream().filter(animal1 -> animal1.equals(animal)).findFirst().get();
    }

    public Shelter updateShelter(Long shelterId, Shelter shelterToUpdate) {
        shelterRepository.findById(shelterId).get();
        shelterToUpdate.setId(shelterId);
        return shelterRepository.save(shelterToUpdate);
    }

    public void delete(Long id) {
        shelterRepository.deleteById(id);
    }

}
