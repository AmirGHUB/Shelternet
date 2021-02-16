package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Shelter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
}
