package com.galvanize.shelternet.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.controller.AdoptionApplicationController;
import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AnimalRepository;
import com.galvanize.shelternet.services.AdoptionApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdoptionApplicationController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AdoptionApplicationRestdocs {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdoptionApplicationService adoptionApplicationService;

    @Mock
    private AnimalRepository animalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void submitAdoptionApplication() throws Exception {
        Animal animal = new Animal("DOGGY", "DOG", LocalDate.of(2020, 4, 15), "M", "WHITE");
        animal.setId(2L);
        when(animalRepository.save(any())).thenReturn(animal);
        Animal animalSaved = animalRepository.save(animal);

        AdoptionApplication adoptionApplication = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", animalSaved.getId());
        adoptionApplication.setId(1L);
        when(adoptionApplicationService.submitAdoptionApplication(any())).thenReturn(Optional.of(adoptionApplication));
        adoptionApplicationService.submitAdoptionApplication(adoptionApplication);

        mockMvc.perform(post("/application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", animalSaved.getId()))))
                .andExpect(status().isCreated())
                .andDo(document("adoption-application", responseFields(
                        fieldWithPath("id").description("Id of the application"),
                        fieldWithPath("name").description("Name of the customer"),
                        fieldWithPath("address").description("Address of the customer"),
                        fieldWithPath("phoneNumber").description("Phone number of the customer"),
                        fieldWithPath("animalId").description("Id of the animal to be adopted"),
                        fieldWithPath("status").description("Status of the application")
                        ),
                        requestFields(
                                fieldWithPath("id").ignored(),
                                fieldWithPath("name").description("Name of the customer"),
                                fieldWithPath("address").description("Address of the customer"),
                                fieldWithPath("phoneNumber").description("Phone number of the customer"),
                                fieldWithPath("animalId").description("Id of the animal to be adopted"),
                                fieldWithPath("status").description("Status of the application"))));
    }
}
