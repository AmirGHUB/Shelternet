package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.*;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShelternetService {

    private ShelterRepository shelterRepository;

    public ShelternetService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public ShelterDto registerShelter(RegisterShelterDto registerShelterDto) {
        Shelter shelter = new Shelter(registerShelterDto.getName(), registerShelterDto.getMaxCapacity());
        return mapToDto(shelterRepository.save(shelter));
    }

    public List<ShelterTrimmedDto> getAllShelters() {
        return shelterRepository.findAll()
                .stream()
                .map(this::mapToTrimmedDto)
                .collect(Collectors.toList());
    }

    public ShelterDto getShelterDetails(Long id) {
        return mapToDto(shelterRepository.getOne(id));
    }

    public AnimalDto surrenderAnimal(Long id, AnimalDto animalDto) {
        Shelter shelter = shelterRepository.getOne(id);
        Animal animal = AnimalMapper.mapToEntity(animalDto);
        shelter.addAnimal(animal);
        animal.setShelter(shelter);
        shelter = shelterRepository.save(shelter);

        return AnimalMapper.mapToDto(
                shelter.getAnimals()
                        .stream()
                        .filter(a -> a.equals(animal))
                        .findFirst()
                        .get()
        );
    }

    public ShelterDto updateShelter(Long shelterId, Shelter shelterToUpdate) {
        Shelter retrievedShelter = shelterRepository.getOne(shelterId);
        shelterToUpdate.setId(shelterId);
        shelterToUpdate.setAnimals(retrievedShelter.getAnimals());
        return mapToDto(shelterRepository.save(shelterToUpdate));
    }

    public void delete(Long id) {
        shelterRepository.deleteById(id);
    }

    private ShelterDto mapToDto(Shelter shelter) {
        return new ShelterDto(shelter.getId(), shelter.getName(), getCurrentCapacity(shelter), shelter.getMaxCapacity(),shelter.getAnimals());
    }

    private ShelterTrimmedDto mapToTrimmedDto(Shelter shelter) {
        return new ShelterTrimmedDto(shelter.getId(), shelter.getName(), getCurrentCapacity(shelter), shelter.getMaxCapacity());
    }

    public boolean transferAnimal(AnimalTransfer animalTransfer) {
        Shelter shelterToTransferFrom = shelterRepository.findById(animalTransfer.getShelterIdFrom()).get();
        Animal animalToTransfer = getAnimalFromShelter(shelterToTransferFrom, animalTransfer.getAnimalId()).get();
        Shelter shelterToTransferTo = shelterRepository.findById(animalTransfer.getShelterIdTo()).get();

        if (getCurrentCapacity(shelterToTransferTo) > 0) {
            shelterToTransferFrom.getAnimals().remove(animalToTransfer);
            shelterToTransferTo.addAnimal(animalToTransfer);
            shelterRepository.save(shelterToTransferFrom);
            shelterRepository.save(shelterToTransferTo);
            return true;
        }
        return false;
    }

    public List<Animal> getAnimalsByShelterId(Long shelterId) {
        return shelterRepository.getOne(shelterId).getAnimals()
                .stream()
                .filter(animal -> !animal.getStatus().equals("ADOPTED"))
                .collect(Collectors.toList());
    }

    private Optional<Animal> getAnimalFromShelter(Shelter shelter, Long animalId) {
        return shelter.getAnimals().stream()
                .filter(animal -> animal.getId().equals(animalId))
                .findFirst();
    }

    private Integer getCurrentCapacity(Shelter shelter) {
        long animalsOnSite = shelter.getAnimals().stream()
                .filter(animal -> animal.getStatus().equals("AVAILABLE") || animal.getStatus().equals("ADOPTION_PENDING"))
                .count();
        return shelter.getMaxCapacity() - (int) animalsOnSite;
    }
}

