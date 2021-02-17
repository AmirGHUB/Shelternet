package com.galvanize.shelternet.services;

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

    public Optional<Shelter>  getShelterDetails(Long id) {
        return shelterRepository.findById(id);
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
