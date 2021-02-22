package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.AnimalDto;
import com.galvanize.shelternet.model.AnimalTransfer;
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

import static org.junit.jupiter.api.Assertions.*;
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

        ShelterDto expected = new ShelterDto(1L, "SHELTER1", 10, new ArrayList<>());
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
        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        shelter.setId(1L);
        shelter.addAnimal(animal);

        when(shelterRepository.getOne(shelter.getId())).thenReturn(shelter);

        ShelterDto actual = shelternetService.getShelterDetails(shelter.getId());
        ShelterDto expected = new ShelterDto(1L, "SHELTER1", 9, List.of(animal));

        assertEquals(expected, actual);

        verify(shelterRepository, times(1)).getOne(shelter.getId());

    }

    @Test
    public void getShelterDetails_SomeAnimalsNotOnSite() {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("Cat", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal3 = new Animal("Cat", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal1.setOnsite(false);
        animal2.setOnsite(true);
        animal3.setOnsite(true);
        shelter.setId(1L);

        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);
        shelter.addAnimal(animal3);

        when(shelterRepository.getOne(shelter.getId())).thenReturn(shelter);

        ShelterDto actual = shelternetService.getShelterDetails(shelter.getId());

        assertEquals(8, actual.getCapacity());
    }

    @Test
    public void acceptSurrenderedAnimals() {
        Shelter shelter = new Shelter("SHELTER1", 10);
        AnimalDto animalDto = new AnimalDto(1L, "Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black", null);
        shelter.addAnimal(new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black"));
        when(shelterRepository.getOne(shelter.getId())).thenReturn(shelter);
        when(shelterRepository.save(shelter)).thenReturn(shelter);

        AnimalDto actualAnimal = shelternetService.surrenderAnimal(shelter.getId(), animalDto);

        assertEquals(animalDto, actualAnimal);

        verify(shelterRepository, times(1)).getOne(shelter.getId());
        verify(shelterRepository, times(1)).save(shelter);


    }

    @Test
    public void updateShelter_looksForExistingShelterAndCallsSaveOnShelterRepo() {
        Shelter shelterToUpdate = new Shelter("SHELTER1", 10);
        Shelter updatedShelter = new Shelter("SHELTER1", 10);
        Shelter existingShelter = new Shelter("SHELTER0", 20);
        updatedShelter.setId(1L);

        when(shelterRepository.getOne(1L)).thenReturn(existingShelter);
        when(shelterRepository.save(updatedShelter)).thenReturn(updatedShelter);

        ShelterDto actual = shelternetService.updateShelter(1L, shelterToUpdate);

        ShelterDto expected = new ShelterDto(1L, "SHELTER1", 10, new ArrayList<>());

        assertEquals(expected, actual);
        verify(shelterRepository, times(1)).getOne(1L);
        verify(shelterRepository, times(1)).save(updatedShelter);
        verifyNoMoreInteractions(shelterRepository);
    }

    @Test
    public void deleteShelterById() {
        shelternetService.delete(1L);
        verify(shelterRepository).deleteById(1L);
    }

    @Test
    public void transferAnimal_transfersAnimalBetweenShelters() {
        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal.setId(2L);
        Shelter shelter1 = new Shelter("shelterToTransferFrom", 5);
        shelter1.setId(1L);
        shelter1.addAnimal(animal);
        Shelter shelter2 = new Shelter("shelterToTransferTo", 5);
        shelter2.setId(3L);

        Shelter shelter1Updated = new Shelter("shelterToTransferFrom", 5);
        shelter1Updated.setId(1L);
        shelter1Updated.setAnimals(new ArrayList<>());
        Shelter shelter2Updated = new Shelter("shelterToTransferTo", 5);
        shelter2Updated.setId(3L);
        shelter2Updated.addAnimal(animal);

        when(shelterRepository.findById(1L)).thenReturn(Optional.of(shelter1));
        when(shelterRepository.findById(3L)).thenReturn(Optional.of(shelter2));
        when(shelterRepository.save(shelter1Updated)).thenReturn(null);
        when(shelterRepository.save(shelter2Updated)).thenReturn(null);

        AnimalTransfer animalTransfer = new AnimalTransfer(1L, 3L, 2L);

        boolean result = shelternetService.transferAnimal(animalTransfer);

        assertTrue(result);
        verify(shelterRepository).save(shelter1Updated);
        verify(shelterRepository).save(shelter2Updated);
        verifyNoMoreInteractions(shelterRepository);
    }


    @Test
    public void transferAnimal_returnsFalseIfShelterToTransferToIsFull() {
        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal.setId(2L);
        Shelter shelter1 = new Shelter("shelterToTransferFrom", 5);
        shelter1.setId(1L);
        shelter1.addAnimal(animal);
        Shelter shelter2 = new Shelter("shelterToTransferTo", 1);
        shelter2.addAnimal(animal);
        shelter2.setId(3L);

        when(shelterRepository.findById(1L)).thenReturn(Optional.of(shelter1));
        when(shelterRepository.findById(3L)).thenReturn(Optional.of(shelter2));

        AnimalTransfer animalTransfer = new AnimalTransfer(1L, 3L, 2L);

        boolean result = shelternetService.transferAnimal(animalTransfer);

        assertFalse(result);
        verifyNoMoreInteractions(shelterRepository);
    }

    @Test
    public void getAnimalsByShelterId() {
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("Cat", "AfricanCat", LocalDate.of(2021, 2, 1), "M", "black");
        Animal animal3 = new Animal("Cat", "Tabby", LocalDate.of(2016, 2, 1), "F", "white");
        animal3.setStatus("ADOPTED");
        List<Animal> expected = List.of(animal1, animal2);
        Shelter shelter = new Shelter("Shelter1", 50);
        shelter.setId(1L);
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);
        shelter.addAnimal(animal3);

        when(shelterRepository.getOne(any())).thenReturn(shelter);

        List<Animal> actual = shelternetService.getAnimalsByShelterId(shelter.getId());

        verify(shelterRepository).getOne(shelter.getId());

        assertEquals(expected, actual);
    }
}
