package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.AnimalDto;
import com.galvanize.shelternet.model.AnimalTransfer;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.model.ShelterDto;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShelternetService {

    private ShelterRepository shelterRepository;

    ModelMapper modelMapper = new ModelMapper();

    public ShelternetService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public ShelterDto registerShelter(Shelter shelter) {
        return mapToDto(shelterRepository.save(shelter));
    }

    public List<ShelterDto> getAllShelters() {
        return shelterRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ShelterDto getShelterDetails(Long id) {
        return mapToDto(shelterRepository.getOne(id));
    }

    public AnimalDto surrenderAnimal(Long id, AnimalDto animalDto) {
        Shelter shelter = shelterRepository.getOne(id);
        Animal animal = modelMapper.map(animalDto, Animal.class);
        shelter.addAnimal(animal);
        animal.setShelter(shelter);
        shelter = shelterRepository.save(shelter);

        return modelMapper.map(
                shelter.getAnimals()
                        .stream()
                        .filter(a -> a.equals(animal))
                        .findFirst()
                        .get(),
                AnimalDto.class
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
        return new ShelterDto(shelter.getId(), shelter.getName(), getCurrentCapacity(shelter), shelter.getAnimals());
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
        return shelterRepository.getOne(shelterId).getAnimals();
    }

    private Optional<Animal> getAnimalFromShelter(Shelter shelter, Long animalId) {
        return shelter.getAnimals().stream()
                .filter(animal -> animal.getId().equals(animalId))
                .findFirst();
    }

    private Integer getCurrentCapacity(Shelter shelter) {
        return shelter.getMaxCapacity() - shelter.getAnimals().size();
    }
}

