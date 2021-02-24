package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.AdoptionApplicationDto;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AdoptionApplicationRepository;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdoptionApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AdoptionApplicationRepository adoptionApplicationRepository;

    @BeforeEach
    public void setUp() {
        this.adoptionApplicationRepository.deleteAll();
        this.animalRepository.deleteAll();
    }

    @Test
    public void submitAdoptionApplication() throws Exception {
        Animal animal = new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE");

        Animal animalSaved = animalRepository.save(animal);

        AdoptionApplicationDto adoptionApplicationDto = new AdoptionApplicationDto(1L,"JOHN", "5131 W Thunderbird Rd.",
                "602-444-4444", animalSaved.getId(),"PENDING");

        mockMvc.perform(post("/applications")
                .content(objectMapper.writeValueAsString(adoptionApplicationDto))
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("JOHN"))
                .andExpect(jsonPath("$.address").value("5131 W Thunderbird Rd."))
                .andExpect(jsonPath("$.phoneNumber").value("602-444-4444"))
                .andExpect(jsonPath("$.animalId").value(animalSaved.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));
        AdoptionApplication application = adoptionApplicationRepository.findAll().get(0);
        assertEquals("PENDING",application.getStatus());
    }

    @Test
    public void submitAdoptionApplication_WhenAnimalDoesntExists() throws Exception {
        AdoptionApplication adoptionApplication = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);

        mockMvc.perform(post("/applications")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(objectMapper.writeValueAsString(adoptionApplication))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllApplications() throws Exception {
        AdoptionApplication adoptionApplication1 = adoptionApplicationRepository.save(new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L));
        AdoptionApplication adoptionApplication2 = adoptionApplicationRepository.save(new AdoptionApplication("Mark", "another address", "876-990-7661", 4L));
        AdoptionApplication adoptionApplication3 = adoptionApplicationRepository.save(new AdoptionApplication("Jane", "yet another address", "145-640-9900", 5L));
        List<AdoptionApplication> applications = List.of(adoptionApplication1, adoptionApplication2, adoptionApplication3);
        String expected = objectMapper.writeValueAsString(applications);

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    public void updateStatus_approvesApplication() throws Exception {
        Animal animal = animalRepository.save(new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE"));
        AdoptionApplication adoptionApplication1 = adoptionApplicationRepository.save(new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", animal.getId()));

        mockMvc.perform(put("/applications/{id}/update-status?isApproved=true", adoptionApplication1.getId())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1")))
                .andExpect(status().isOk());

        assertEquals("ADOPTED", animalRepository.findById(animal.getId()).orElseThrow().getStatus());
        assertEquals("APPROVED", adoptionApplicationRepository.findById(adoptionApplication1.getId()).orElseThrow().getStatus());
    }

    @Test
    public void updateStatus_rejectsApplication() throws Exception {
        Animal animal = animalRepository.save(new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE"));
        AdoptionApplication adoptionApplication1 = adoptionApplicationRepository.save(new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", animal.getId()));

        mockMvc.perform(put("/applications/{id}/update-status?isApproved=false", adoptionApplication1.getId())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1")))
                .andExpect(status().isOk());

        assertEquals("AVAILABLE", animalRepository.findById(animal.getId()).orElseThrow().getStatus());
        assertEquals("REJECTED", adoptionApplicationRepository.findById(adoptionApplication1.getId()).orElseThrow().getStatus());
    }
}
