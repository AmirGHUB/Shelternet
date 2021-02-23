package com.galvanize.shelternet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.AdoptionApplicationDto;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AdoptionApplicationRepository;
import com.galvanize.shelternet.repository.AnimalRepository;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdoptionApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AdoptionApplicationRepository adoptionApplicationRepository;

    @Test
    public void submitAdoptionApplication() throws Exception {
        Animal animal1 = new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE");
        Animal animal2 = new Animal("CATY", "CAT", LocalDate.of(2020, 4, 15), "F", "BLACK");

        List<Animal> animalSaved = animalRepository.saveAll(List.of(animal1,animal2));

        List<Long> animalIds = animalSaved.stream().map(a -> a.getId()).collect(Collectors.toList());

        AdoptionApplicationDto adoptionApplicationDto = new AdoptionApplicationDto(1L,"JOHN", "5131 W Thunderbird Rd.",
                "602-444-4444", animalIds,"PENDING");

        mockMvc.perform(post("/applications")
                .content(objectMapper.writeValueAsString(adoptionApplicationDto))
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("JOHN"))
                .andExpect(jsonPath("$.address").value("5131 W Thunderbird Rd."))
                .andExpect(jsonPath("$.phoneNumber").value("602-444-4444"))
                .andExpect(jsonPath("$.animalIds").value(animalIds))
                .andExpect(jsonPath("$.status").value("PENDING"));
        AdoptionApplication application = adoptionApplicationRepository.findAll().get(0);
        assertEquals("PENDING",application.getStatus());
    }

    @Test
    public void submitAdoptionApplication_WhenAnimalDoesntExists() throws Exception {
        Animal animal1 = new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE");
        Animal animal2 = new Animal("CATY", "CAT", LocalDate.of(2020, 4, 15), "F", "BLACK");
        AdoptionApplication adoptionApplication = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", List.of(animal1,animal2));

        mockMvc.perform(post("/applications")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(objectMapper.writeValueAsString(adoptionApplication))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllApplications() throws Exception {
        Animal animal1 = new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE");
        Animal animal2 = new Animal("CATY", "CAT", LocalDate.of(2020, 4, 15), "F", "BLACK");
        Animal animal3 = new Animal("RABI", "RABBIT", LocalDate.of(2020, 4, 15), "M", "GREY");
        AdoptionApplication adoptionApplication1 = adoptionApplicationRepository.save(new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", List.of(animal1)));
        AdoptionApplication adoptionApplication2 = adoptionApplicationRepository.save(new AdoptionApplication("Mark", "another address", "876-990-7661", List.of(animal2)));
        AdoptionApplication adoptionApplication3 = adoptionApplicationRepository.save(new AdoptionApplication("Jane", "yet another address", "145-640-9900", List.of(animal3)));
        List<AdoptionApplication> applications = List.of(adoptionApplication1, adoptionApplication2, adoptionApplication3);
        String expected = objectMapper.writeValueAsString(applications);

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
    }

    @Test
    public void updateStatus_approvesApplication() throws Exception {
        Animal animal = animalRepository.save(new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE"));
        AdoptionApplication adoptionApplication1 = adoptionApplicationRepository.save(new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", List.of(animal)));

        mockMvc.perform(put("/applications/{id}/update-status?isApproved=true", adoptionApplication1.getId())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1")))
                .andExpect(status().isOk());

        assertEquals("ADOPTED", animalRepository.findById(animal.getId()).get().getStatus());
        assertEquals("APPROVED", adoptionApplicationRepository.findById(adoptionApplication1.getId()).get().getStatus());
    }

    @Test
    public void updateStatus_rejectsApplication() throws Exception {
        Animal animal = animalRepository.save(new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE"));
        AdoptionApplication adoptionApplication1 = adoptionApplicationRepository.save(new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", List.of(animal)));

        mockMvc.perform(put("/applications/{id}/update-status?isApproved=false", adoptionApplication1.getId())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1")))
                .andExpect(status().isOk());

        assertEquals("AVAILABLE", animalRepository.findById(animal.getId()).get().getStatus());
        assertEquals("REJECTED", adoptionApplicationRepository.findById(adoptionApplication1.getId()).get().getStatus());
    }
}
