package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.model.Shelternet;
import com.galvanize.shelternet.repository.ShelternetRepository;
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
public class ShelternetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShelternetRepository shelternetRepository;

    @Test
    public void homeTest() throws Exception {
        mockMvc.perform(get("/home")).andExpect(status().isOk())
                .andExpect(content().string("Welcome to Shelternet"));
    }

    @Test
    public void registerShelterTest() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Shelternet shelternet = new Shelternet();
        Shelternet shelternetExisted = shelternetRepository.save(shelternet);
        shelternet.addShelter(shelter);
        mockMvc.perform(post("/shelter" + "/" + shelternetExisted.getId())
                .content(objectMapper.writeValueAsString(shelter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SHELTER1"))
                .andExpect(jsonPath("$.capacity").value(10));
    }
}
