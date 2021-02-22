package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.Animal;
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

    public AdoptionApplication submitAdoptionApplication(AdoptionApplication adoptionApplication) {
       Optional<Animal> animal = animalRepository.findById(adoptionApplication.getAnimalId());
       if(!animal.isPresent() || (animal.get().getStatus() != null && animal.get().getStatus().equals("ADOPTION_PENDING"))) {
           return null;
       }
       animal.get().setStatus("ADOPTION_PENDING");
       animalRepository.save(animal.get());
       return adoptionApplicationRepository.save(adoptionApplication);
    }

    public List<AdoptionApplication> getAllApplications() {
        return adoptionApplicationRepository.findAll();
    }

    public void updateStatus(Long applicationId, boolean isApproved) {
        AdoptionApplication adoptionApplication = adoptionApplicationRepository.findById(applicationId).get();
        Animal animal = animalRepository.findById(adoptionApplication.getAnimalId()).get();

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
