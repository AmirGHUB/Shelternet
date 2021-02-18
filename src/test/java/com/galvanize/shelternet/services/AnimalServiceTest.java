package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {
    @Mock
    AnimalRepository animalRepository;

    @InjectMocks
    AnimalService animalService;

    @Test
    public void getAllAnimals(){
        Animal animal1 = new Animal("Micro","Dog", LocalDate.now(),"M","Brown");
        Animal animal2 = new Animal("Sammy","Dog",LocalDate.now(),"M","Black");
        Animal animal3 = new Animal("Hunter","Dog",LocalDate.now(),"M","Brown");
        List<Animal> animals1 = List.of(animal1,animal2,animal3);
        when(animalRepository.findAll()).thenReturn(animals1);
        List<Animal> result = animalService.getAllAnimals();

    }

}
