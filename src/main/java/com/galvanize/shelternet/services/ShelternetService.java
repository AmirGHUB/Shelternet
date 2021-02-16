package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.springframework.stereotype.Service;

@Service
public class ShelternetService {

    private ShelterRepository shelterRepository;

    public ShelternetService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public Shelter registerShelter(Shelter shelter) {
        return shelterRepository.save(shelter);
    }
}
