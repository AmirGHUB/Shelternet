package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.repository.AnimalRepository;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AnimalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalRepository animalRepository;

    @Test
    public void getAllAnimals() throws Exception {
        Animal animal1 = animalRepository.save(new Animal("Dog","Dalmention", LocalDate.of(2009,04,1),"M", "black"));
        Animal animal2 = animalRepository.save(new Animal("Cat","AfricanCat", LocalDate.of(2021,02,1),"M", "black"));
        Animal animal3 = animalRepository.save(new Animal("Tiger","BengalTiger", LocalDate.of(2015,02,1),"M","White"));
        List<Animal> expected = List.of(animal1,animal2,animal3);

        String expectedString = objectMapper.writeValueAsString(expected);
        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedString));
    }
}
