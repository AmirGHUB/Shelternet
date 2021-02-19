package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.AnimalDto;
import com.galvanize.shelternet.model.AnimalRequestIds;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {
    @Mock
    AnimalRepository animalRepository;

    @InjectMocks
    AnimalService animalService;

    @Test
    public void getAllAnimals() {
        Animal animal1 = new Animal("Micro", "Dog", LocalDate.now(), "M", "Brown");
        Animal animal2 = new Animal("Sammy", "Dog", LocalDate.now(), "M", "Black");
        Animal animal3 = new Animal("Hunter", "Dog", LocalDate.now(), "M", "Brown");
        List<Animal> animals1 = List.of(animal1, animal2, animal3);
        when(animalRepository.findAll()).thenReturn(animals1);
        List<Animal> result = animalService.getAllAnimals();
        assertEquals(animals1, result);

    }

    @Test
    public void request() {
        Animal animal1 = new Animal("Micro", "Dog", LocalDate.now(), "M", "Brown");
        animal1.setId(1L);
        Animal animal2 = new Animal("Sammy", "Dog", LocalDate.now(), "M", "Black");
        animal2.setId(2L);

        when(animalRepository.getOne(1L)).thenReturn(animal1);
        when(animalRepository.getOne(2L)).thenReturn(animal2);

        AnimalRequestIds animalRequestIds = new AnimalRequestIds(List.of(animal1.getId(), animal2.getId()));

        List<AnimalDto> actual = animalService.request(animalRequestIds);
        AnimalDto animalDto1 = new AnimalDto(1L, "Micro", "Dog", LocalDate.now(), "M", "Brown");
        AnimalDto animalDto2 = new AnimalDto(2L, "Sammy", "Dog", LocalDate.now(), "M", "Black");

        List<AnimalDto> expected = List.of(animalDto1, animalDto2);
        assertEquals(expected, actual);

        Animal animal1Updated = new Animal("Micro", "Dog", LocalDate.now(), "M", "Brown");
        Animal animal2Updated = new Animal("Sammy", "Dog", LocalDate.now(), "M", "Black");
        animal1Updated.setOnsite(false);
        animal2Updated.setOnsite(false);
        verify(animalRepository).saveAll(List.of(animal1Updated, animal2Updated));
    }
}
