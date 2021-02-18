package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShelterRepository shelterRepository;


    @Test
    public void registerShelterTest() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);

        mockMvc.perform(post("/shelter")
                .content(objectMapper.writeValueAsString(shelter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SHELTER1"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    public void getAllShelters() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Shelter shelter2 = new Shelter("SHELTER2", 20);

        mockMvc.perform(post("/shelter")
                .content(objectMapper.writeValueAsString(shelter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/shelter")
                .content(objectMapper.writeValueAsString(shelter2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc
                .perform(get("/shelter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("[0].name").value("SHELTER1"))
                .andExpect(jsonPath("[0].capacity").value(10))
                .andExpect(jsonPath("[1].name").value("SHELTER2"))
                .andExpect(jsonPath("[1].capacity").value(20));

    }

    @Test
    public void getShelterDetails() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Animal animal1 = new Animal("Dog","Dalmention", LocalDate.of(2009,4,1),"M", "black");
        Animal animal2 = new Animal("hob","wildcat", LocalDate.of(2010,5,2),"F", "white");
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);

        MvcResult result = mockMvc.perform(post("/shelter")
                .content(objectMapper.writeValueAsString(shelter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        Shelter shelterResult = objectMapper.readValue(jsonResult, Shelter.class);

        mockMvc
                .perform(get("/shelter" + "/" + shelterResult.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SHELTER1"))
                .andExpect(jsonPath("$.capacity").value(8));

    }


    @Test
    public void acceptSurrenderedAnimals() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);

        MvcResult result = mockMvc.perform(post("/shelter")
                .content(objectMapper.writeValueAsString(shelter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        Shelter shelterResult = objectMapper.readValue(jsonResult, Shelter.class);

        Animal animal = new Animal("Dog","Dalmention", LocalDate.of(2009,4,1),"M", "black");

        mockMvc.perform(post("/shelter/"+shelterResult.getId()+"/animal/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Dog"))
                .andExpect(jsonPath("$.species").value("Dalmention"))
                .andExpect(jsonPath("$.birthDate").value("2009-04-01"))
                .andExpect(jsonPath("$.sex").value("M"))
                .andExpect(jsonPath("$.color").value("black"));

        mockMvc
                .perform(get("/shelter/" + shelterResult.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(9));

    }

    @Test
    public void updateShelter() throws Exception {
        Shelter existingShelter = shelterRepository.save(new Shelter("Original Shelter", 20));
        Shelter shelterToUpdate = new Shelter("Updated Shelter", 10);
        String shelterString = objectMapper.writeValueAsString(shelterToUpdate);

        mockMvc.perform(put("/shelter/" + existingShelter.getId())
                .content(shelterString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingShelter.getId()))
                .andExpect(jsonPath("$.name").value("Updated Shelter"))
                .andExpect(jsonPath("$.capacity").value(10));
    }

    @Test
    public void deleteShelterById() throws Exception {
        Shelter existingShelter = shelterRepository.save(new Shelter("Original Shelter", 20));
        mockMvc.perform(delete("/shelter/" + existingShelter.getId()))
                .andExpect(status().isOk());
        assertEquals(0, shelterRepository.findAll().size());
    }


}

