package com.galvanize.shelternet.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.controller.AnimalController;
import com.galvanize.shelternet.model.*;
import com.galvanize.shelternet.services.AnimalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnimalController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
public class AnimalRestdocs {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnimalService animalService;

    @Test
    public void getAllAnimals() throws Exception {
        Animal animal1 = new Animal("Micro", "Dog", LocalDate.now(), "M", "Brown");
        Animal animal2 = new Animal("Sammy", "Dog", LocalDate.now(), "M", "Black");
        Animal animal3 = new Animal("Hunter", "Dog", LocalDate.now(), "M", "Brown");
        animal1.setId(1L);
        animal2.setId(2L);
        animal3.setId(3L);
        List<Animal> animals1 = List.of(animal1, animal2, animal3);
        when(animalService.getAllAnimals()).thenReturn(animals1);

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andDo(document("get-all-animals", responseFields(
                        fieldWithPath("[*].id").description("The ID of the animal."),
                        fieldWithPath("[*].name").description("The Name of the animal."),
                        fieldWithPath("[*].species").description("The Species of the animal."),
                        fieldWithPath("[*].birthDate").description("The BirthDate of the animal."),
                        fieldWithPath("[*].sex").description("The Sex of the animal."),
                        fieldWithPath("[*].color").description("The Color of the animal."),
                        fieldWithPath("[*].notes").description("Notes on the Animal"),
                        fieldWithPath("[*].status").description("Adoption status of the Animal")
                )));
    }

    @Test
    public void animalRequest() throws Exception {
        AnimalDto animal1 = new AnimalDto(1L, "Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black", null);
        AnimalDto animal2 = new AnimalDto(2L, "Cat", "Tabby", LocalDate.of(2010, 4, 1), "M", "white", null);
        AnimalDto animal3 = new AnimalDto(3L, "Dog", "CockerSpaniel", LocalDate.of(2006, 4, 1), "F", "red", null);
        List<AnimalDto> animalList = List.of(animal1, animal2, animal3);

        AnimalRequestIds animalRequestIds = new AnimalRequestIds(List.of(animal1.getId(), animal2.getId(), animal3.getId()));

        when(animalService.request(any())).thenReturn(animalList);
        mockMvc.perform(post("/animals/request/")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animalRequestIds)))
                .andExpect(status().isOk())
                .andDo(document("request-animals-by-ids", responseFields(
                        fieldWithPath("[*].id").description("The ID of the animal."),
                        fieldWithPath("[*].name").description("The Name of the animal."),
                        fieldWithPath("[*].species").description("The Species of the animal."),
                        fieldWithPath("[*].birthDate").description("The BirthDate of the animal."),
                        fieldWithPath("[*].sex").description("The Sex of the animal."),
                        fieldWithPath("[*].color").description("The Color of the animal."),
                        fieldWithPath("[*].notes").description("Notes on the Animal")
                ), requestFields(fieldWithPath("animalIds").description("Requested animal ids."))));
    }

    @Test
    public void requestAnimalsBackFromPetStore() throws Exception {
        AnimalRequestIds animalRequestIds = new AnimalRequestIds(List.of(1L, 2L));

        when(animalService.requestAnimalsBack(animalRequestIds)).thenReturn(true);

        mockMvc.perform(post("/animals/return-request")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animalRequestIds)))
                .andExpect(status().isOk())
                .andDo(document("return-animals-back-to-shelter",
                        requestFields(
                                fieldWithPath("animalIds").description("Requested animal ids."))));
    }

    @Test
    public void adoptAnimals() throws Exception {
        AnimalRequestIds requestIds = new AnimalRequestIds(List.of(1L, 2L, 3L));
        when(animalService.adoptAnimals(requestIds.getAnimalIds())).thenReturn(true);
        String ids = objectMapper.writeValueAsString(requestIds);
        mockMvc.perform(post("/animals/adopted")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(ids))
                .andExpect(status().isOk())
                .andDo(document("adopt-animals", requestFields(
                        fieldWithPath("animalIds[*]").description("The id of the animal to adopt")
                )));
    }

    @Test
    public void returnAnimalsFromPetStore() throws Exception {
        List<AnimalReturnDto> returnedAnimals = List.of(new AnimalReturnDto(1L, "Bob is super friendly"),
                new AnimalReturnDto(2L, "Seems to have fleas"));

        AnimalReturn animalReturn = new AnimalReturn(returnedAnimals);

        when(animalService.returnAnimalsToShelter(any())).thenReturn(true);

        mockMvc.perform(post("/animals/return")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animalReturn)))
                .andExpect(status().isOk())
                .andDo(document("return-animals-from-petstore", requestFields(
                        fieldWithPath("animals[*].id").description("The id of the animal to return"),
                        fieldWithPath("animals[*].notes").description("notes on the animal")
                )));
    }
}

