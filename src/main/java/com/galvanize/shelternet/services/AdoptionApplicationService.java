package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.AdoptionApplicationDto;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AdoptionApplicationRepository;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdoptionApplicationService {

    private AdoptionApplicationRepository adoptionApplicationRepository;

    private AnimalRepository animalRepository;

    public AdoptionApplicationService(AdoptionApplicationRepository adoptionApplicationRepository, AnimalRepository animalRepository) {
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.animalRepository = animalRepository;
    }

    public AdoptionApplicationDto submitAdoptionApplication(AdoptionApplicationDto adoptionApplicationDto) {
        List<Animal>  animals = new ArrayList<>();
        for(Long id: adoptionApplicationDto.getAnimalIds()) {
            Optional<Animal> animal = animalRepository.findById(id);
            animals.add(animal.get());
            if(!animal.isPresent() || !animal.get().getStatus().equals("AVAILABLE")) {
                return null;
            }
            animal.get().setStatus("ADOPTION_PENDING");
            animalRepository.save(animal.get());
        }
       AdoptionApplication adoptionApplication = new AdoptionApplication(adoptionApplicationDto.getName(), adoptionApplicationDto.getAddress(),
               adoptionApplicationDto.getPhoneNumber(), animals);
       adoptionApplication.setStatus("PENDING");
       return mapToApplicationDto(adoptionApplicationRepository.save(adoptionApplication));

    }

    private AdoptionApplicationDto mapToApplicationDto(AdoptionApplication adoptionApplication) {
        List<Long> ids = new ArrayList<>();
        for(Animal a: adoptionApplication.getAnimals()) {
            ids.add(a.getId());
        }
        return new AdoptionApplicationDto(adoptionApplication.getId(), adoptionApplication.getName(), adoptionApplication.getAddress(),
                adoptionApplication.getPhoneNumber(), ids, adoptionApplication.getStatus());
    }


    public List<AdoptionApplication> getAllApplications() {
        return adoptionApplicationRepository.findAll();
    }

    public void updateStatus(Long applicationId, boolean isApproved) {
        AdoptionApplication adoptionApplication = adoptionApplicationRepository.findById(applicationId).get();
        for(Animal a: adoptionApplication.getAnimals()) {
            Animal animal = animalRepository.findById(a.getId()).get();
            if(isApproved) {
                adoptionApplication.setStatus("APPROVED");
                animal.setStatus("ADOPTED");
            } else {
                adoptionApplication.setStatus("REJECTED");
                animal.setStatus("AVAILABLE");
            }

            adoptionApplicationRepository.save(adoptionApplication);
            animalRepository.save(animal);
        }
    }


}
