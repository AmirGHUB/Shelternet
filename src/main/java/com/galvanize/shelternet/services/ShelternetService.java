package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.model.Shelternet;
import com.galvanize.shelternet.repository.ShelternetRepository;
import org.springframework.stereotype.Service;

@Service
public class ShelternetService {

    private ShelternetRepository shelternetRepository;

    public ShelternetService(ShelternetRepository shelternetRepository) {
        this.shelternetRepository = shelternetRepository;
    }

    public Shelter registerShelter(Shelter shelter, Long id) {
        Shelternet shelternetFounded = shelternetRepository.getOne(id);
        shelternetFounded.addShelter(shelter);
        Shelternet newShelternet = shelternetRepository.save(shelternetFounded);
        return newShelternet.getShelters().get(newShelternet.getShelters().size() - 1);
    }
}
