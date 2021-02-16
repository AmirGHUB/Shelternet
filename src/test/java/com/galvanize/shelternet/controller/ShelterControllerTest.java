package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Shelter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void homeTest() throws Exception {
        mockMvc.perform(get("/home")).andExpect(status().isOk())
                .andExpect(content().string("Welcome to Shelternet"));
    }

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

        List<Shelter> shelters = List.of(shelter,shelter2);

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
                .andExpect(jsonPath("$.*",hasSize(2)))
                .andExpect(jsonPath("[0].name").value("SHELTER1"))
                .andExpect(jsonPath("[0].capacity").value(10))
                .andExpect(jsonPath("[1].name").value("SHELTER2"))
                .andExpect(jsonPath("[1].capacity").value(20));

    }
    @Test
    public void getShelterDetails() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
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
                .andExpect(jsonPath("$.capacity").value(10));


    }
}
