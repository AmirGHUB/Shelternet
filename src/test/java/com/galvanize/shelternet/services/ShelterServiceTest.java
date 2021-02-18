package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.model.ShelterDto;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
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
        shelter.setId(1L);
        when(shelterRepository.save(any())).thenReturn(shelter);

        ShelterDto actual = shelternetService.registerShelter(shelter);

        ShelterDto expected = new ShelterDto(1L,"SHELTER1",10,new ArrayList<>());
        verify(shelterRepository, times(1)).save(shelter);

        assertEquals(expected, actual);
    }

    @Test
    public void getAllShelters() {

        Shelter shelter = new Shelter("SHELTER1", 10);
        Shelter shelter2 = new Shelter("SHELTER2", 20);

        List<Shelter> shelters = List.of(shelter, shelter2);

        when(shelterRepository.findAll()).thenReturn(shelters);

        List<ShelterDto> actualList = shelternetService.getAllShelters();

        assertEquals(shelters.size(), actualList.size());

        verify(shelterRepository, times(1)).findAll();
    }

    @Test
    public void getShelterDetails() {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Animal animal = new Animal("Dog","Dalmention", LocalDate.of(2009,4,1),"M", "black");
        shelter.setId(1L);
        shelter.addAnimal(animal);

        when(shelterRepository.getOne(shelter.getId())).thenReturn(shelter);

        ShelterDto actual = shelternetService.getShelterDetails(shelter.getId());
        ShelterDto expected = new ShelterDto(1L,"SHELTER1",9,List.of(animal));

        assertEquals(expected, actual);

        verify(shelterRepository, times(1)).getOne(shelter.getId());

    }

    @Test
    public void acceptSurrenderedAnimals() {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Animal animal = new Animal("Dog","Dalmention", LocalDate.of(2009,4,1),"M", "black");
        shelter.setId(1L);
        shelter.addAnimal(animal);
        when(shelterRepository.getOne(shelter.getId())).thenReturn(shelter);
        when(shelterRepository.save(shelter)).thenReturn(shelter);

        Animal actualAnimal = shelternetService.surrenderAnimal(shelter.getId(),animal);

        assertEquals(animal,actualAnimal);

        verify(shelterRepository,times(1)).getOne(shelter.getId());
        verify(shelterRepository,times(1)).save(shelter);


    }

    @Test
    public void updateShelter_looksForExistingShelterAndCallsSaveOnShelterRepo() {
        Shelter shelterToUpdate = new Shelter("SHELTER1", 10);
        Shelter updatedShelter = new Shelter("SHELTER1", 10);
        Shelter existingShelter = new Shelter("SHELTER0", 20);
        updatedShelter.setId(1L);

        when(shelterRepository.findById(1L)).thenReturn(Optional.of(existingShelter));
        when(shelterRepository.save(updatedShelter)).thenReturn(updatedShelter);

        ShelterDto actual = shelternetService.updateShelter(1L, shelterToUpdate);

        ShelterDto expected = new ShelterDto(1L,"SHELTER1",10,new ArrayList<>());

        assertEquals(expected, actual);
        verify(shelterRepository, times(1)).findById(1L);
        verify(shelterRepository, times(1)).save(updatedShelter);
        verifyNoMoreInteractions(shelterRepository);
    }

    @Test
    public void deleteShelterById() {
        shelternetService.delete(1L);
        verify(shelterRepository).deleteById(1L);
    }

}
