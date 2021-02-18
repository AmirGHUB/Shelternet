package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.repository.AdoptionApplicationRepository;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.springframework.stereotype.Service;

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

    public Optional<AdoptionApplication> submitAdoptionApplication(AdoptionApplication adoptionApplication) {
       return animalRepository.findById(adoptionApplication.getAnimalId())
               .map(a -> adoptionApplicationRepository.save(adoptionApplication));
    }

    public List<AdoptionApplication> getAllApplications() {
        return adoptionApplicationRepository.findAll();
    }
}
