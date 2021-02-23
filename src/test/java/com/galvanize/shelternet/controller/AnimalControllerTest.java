package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.*;
import com.galvanize.shelternet.repository.AnimalRepository;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AnimalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Test
    public void getAllAnimals() throws Exception {
        Animal animal1 = animalRepository.save(new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black"));
        Animal animal2 = animalRepository.save(new Animal("Cat", "AfricanCat", LocalDate.of(2021, 2, 1), "M", "black"));
        Animal animal3 = animalRepository.save(new Animal("Tiger", "BengalTiger", LocalDate.of(2015, 2, 1), "M", "White"));
        List<Animal> expected = List.of(animal1, animal2, animal3);

        String expectedString = objectMapper.writeValueAsString(expected);
        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedString));
    }

    @Test
    public void animalRequest() throws Exception {
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("Cat", "Tabby", LocalDate.of(2010, 4, 1), "M", "white");
        Animal animal3 = new Animal("Dog", "CockerSpaniel", LocalDate.of(2006, 4, 1), "F", "red");

        Shelter shelter = new Shelter("SHELTER1", 50);
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);
        shelter.addAnimal(animal3);

        shelter = shelterRepository.save(shelter);

        AnimalRequestIds animalRequestIds = new AnimalRequestIds(List.of(animal1.getId(), animal2.getId()));

        String result = mockMvc.perform(post("/animals/request/")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(objectMapper.writeValueAsString(animalRequestIds)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        AnimalDto animalDto1 = new AnimalDto(animal1.getId(), "Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black", null);
        AnimalDto animalDto2 = new AnimalDto(animal2.getId(), "Cat", "Tabby", LocalDate.of(2010, 4, 1), "M", "white", null);
        assertEquals(objectMapper.writeValueAsString(List.of(animalDto1, animalDto2)), result);

        mockMvc
                .perform(get("/shelters" + "/" + shelter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingCapacity").value(49));
    }

    @Test
    public void returnAnimalsFromPetStore() throws Exception {
        Shelter shelter = new Shelter("Dallas Animal Shelter", 20);
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 04, 1), "M", "black");
        Animal animal2 = new Animal("Cat", "Tabby", LocalDate.of(2010, 04, 1), "M", "white");
        animal1.setShelter(shelter);
        animal2.setShelter(shelter);
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);
        animal1 = animalRepository.save(animal1);
        animal2 = animalRepository.save(animal2);

        AnimalRequestIds animalRequestIds = new AnimalRequestIds(List.of(animal1.getId(), animal2.getId()));

        mockMvc.perform(post("/animals/request/")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(objectMapper.writeValueAsString(animalRequestIds)))
                .andExpect(status().isOk());


        List<AnimalReturnDto> returnedAnimals = List.of(new AnimalReturnDto(animal1.getId(), "Bob is super friendly"),
                new AnimalReturnDto(animal2.getId(), "Seems to have fleas"));

        AnimalReturn animalReturn = new AnimalReturn(returnedAnimals);

        mockMvc
                .perform(post("/animals/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                        .content(objectMapper.writeValueAsString(animalReturn)))
                .andExpect(status().isOk());

        Animal fetchedAnimal1 = animalRepository.getOne(animal1.getId());

        assertEquals("Dallas Animal Shelter", fetchedAnimal1.getShelter().getName());
        assertEquals(true, fetchedAnimal1.getOnsite());
        assertEquals("Bob is super friendly", fetchedAnimal1.getNotes());

    }

    @Test
    public void requestAnimalsBackFromPetStore() throws Exception {
        Shelter shelter = new Shelter("Dallas Animal Shelter", 20);
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("Cat", "Tabby", LocalDate.of(2010, 4, 1), "M", "white");
        animal1.setShelter(shelter);
        animal2.setShelter(shelter);
        animal1.setOnsite(false);
        animal2.setOnsite(false);
        animal1.setStatus("NOT AVAILABLE");
        animal2.setStatus("NOT AVAILABLE");
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);
        shelter = shelterRepository.save(shelter);
        animal1 = shelter.getAnimals().get(0);
        animal2 = shelter.getAnimals().get(1);


        AnimalRequestIds animalRequestIds = new AnimalRequestIds(List.of(
                animal1.getId(), animal2.getId())
        );

        mockMvc.perform(post("/animals/return-request")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(objectMapper.writeValueAsString(animalRequestIds)))
                .andExpect(status().isOk());

        animal1 = animalRepository.getOne(animal1.getId());
        animal2 = animalRepository.getOne(animal2.getId());

        assertTrue(animal1.getOnsite());
        assertTrue(animal2.getOnsite());

        assertEquals("AVAILABLE", animal1.getStatus());
        assertEquals("AVAILABLE", animal2.getStatus());
    }

    @Test
    public void adoptAnimals() throws Exception {
        Animal animal1 = animalRepository.save(new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black"));
        Animal animal2 = animalRepository.save(new Animal("Cat", "AfricanCat", LocalDate.of(2021, 2, 1), "M", "black"));
        Animal animal3 = animalRepository.save(new Animal("Tiger", "BengalTiger", LocalDate.of(2015, 2, 1), "M", "White"));
        String ids = objectMapper.writeValueAsString(new AnimalRequestIds(List.of(animal1.getId(), animal2.getId(), animal3.getId())));
        mockMvc.perform(post("/animals/adopted")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(ids))
                .andExpect(status().isOk());
        assertEquals("ADOPTED", animalRepository.findById(animal1.getId()).get().getStatus());
        assertEquals("ADOPTED", animalRepository.findById(animal2.getId()).get().getStatus());
        assertEquals("ADOPTED", animalRepository.findById(animal3.getId()).get().getStatus());
    }
}
