package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelterServiceTest {

    @Mock
    private ShelterRepository shelterRepository;

    @InjectMocks
    private ShelternetService shelternetService;

    @Test
    public void registerShelterTest() {
        Shelter shelter = new Shelter("SHELTER1", 10);

        when(shelterRepository.save(any())).thenReturn(shelter);

        Shelter shelterAdded = shelternetService.registerShelter(shelter);

        verify(shelterRepository, times(1)).save(shelter);

        assertEquals(shelter, shelterAdded);
    }

    @Test
    public void getAllShelters(){

        Shelter shelter = new Shelter("SHELTER1", 10);
        Shelter shelter2 = new Shelter("SHELTER2", 20);

        List<Shelter> shelters = List.of(shelter,shelter2);

        when(shelterRepository.findAll()).thenReturn(shelters);

        List<Shelter> actualList = shelternetService.getAllShelters();

        assertEquals(shelters.size(), actualList.size());

        verify(shelterRepository, times(1)).findAll();
    }

    @Test
    public void getShelterDetails(){
        Shelter shelter = new Shelter("SHELTER1", 10);

        shelter.setId(1L);

        when(shelterRepository.findById(shelter.getId())).thenReturn(Optional.of(shelter));

        Shelter actualShelter = shelternetService.getShelterDetails(shelter.getId()).get();

        assertEquals(shelter,actualShelter);

        verify(shelterRepository,times(1)).findById(shelter.getId());

    }
}
