package com.galvanize.shelternet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShelternetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void homeTest() throws Exception {
        mockMvc.perform(get("/home")).andExpect(status().isOk())
                .andExpect(content().string("Welcome to Shelternet"));
    }
}
