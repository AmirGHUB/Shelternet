package com.galvanize.shelternet.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.controller.ShelterController;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.model.ShelterDto;
import com.galvanize.shelternet.services.ShelternetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShelterController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ShelterRestdocs {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShelternetService shelternetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerShelterRestDocTest() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        shelter.setId(1L);

        ShelterDto expected = new ShelterDto(shelter.getId(), shelter.getName(), shelter.getMaxCapacity(), shelter.getAnimals());
        when(shelternetService.registerShelter(shelter)).thenReturn(expected);

        mockMvc.perform(post("/shelters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Shelter("SHELTER1", 10))))
                .andExpect(status().isCreated())
                .andDo(document("register-shelter",
                        responseFields(
                                fieldWithPath("id").description("Id of the shelter"),
                                fieldWithPath("name").description("Name of the shelter"),
                                fieldWithPath("capacity").description("Capacity of the shelter"),
                                fieldWithPath("animals").description("Animals in the shelter")),
                        requestFields(
                                fieldWithPath("id").ignored(),
                                fieldWithPath("name").description("Name of the shelter"),
                                fieldWithPath("maxCapacity").description("Capacity of the shelter"),
                                fieldWithPath("animals").ignored())));
    }


    @Test
    public void getAllShelters() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Shelter shelter2 = new Shelter("SHELTER2", 20);
        shelter.setId(1L);
        shelter2.setId(2L);

        ShelterDto expected1 = new ShelterDto(shelter.getId(), shelter.getName(), shelter.getMaxCapacity(), shelter.getAnimals());
        ShelterDto expected2 = new ShelterDto(shelter2.getId(), shelter2.getName(), shelter2.getMaxCapacity(), shelter2.getAnimals());
        when(shelternetService.getAllShelters()).thenReturn(List.of(expected1, expected2));

        mockMvc
                .perform(get("/shelters"))
                .andExpect(status().isOk())
                .andDo(document("GetAllShelters", responseFields(
                        fieldWithPath("[*].id").description("The ID of the shelter."),
                        fieldWithPath("[*].name").description("The name of the shelter."),
                        fieldWithPath("[*].capacity").description("The capacity of the shelter."),
                        fieldWithPath("[*].animals").ignored()
                )));
    }

    @Test
    public void getShelterDetails() throws Exception {

        Shelter shelter = new Shelter("SHELTER1", 10);

        shelter.setId(1L);
        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal.setId(1L);
        shelter.addAnimal(animal);

        ShelterDto expected = new ShelterDto(shelter.getId(), shelter.getName(), shelter.getMaxCapacity(), shelter.getAnimals());

        when(shelternetService.getShelterDetails(shelter.getId())).thenReturn(expected);

        mockMvc
                .perform(get("/shelters" + "/" + shelter.getId()))
                .andExpect(status().isOk())
                .andDo(document("GetShelterDetailsById", responseFields(
                        fieldWithPath("id").description("The ID of the shelter."),
                        fieldWithPath("name").description("The name of the shelter."),
                        fieldWithPath("capacity").description("The capacity of the shelter."),
                        fieldWithPath("animals").description("Animals in the Shelter"),
                        fieldWithPath("animals.[*].id").description("Id of the Animal"),
                        fieldWithPath("animals.[*].name").description("Name of the Animal"),
                        fieldWithPath("animals.[*].species").description("Species of the Animal"),
                        fieldWithPath("animals.[*].birthDate").description("Birth Date of the Animal"),
                        fieldWithPath("animals.[*].sex").description("Sex of the Animal"),
                        fieldWithPath("animals.[*].color").description("Color of the Animal")
                )));
    }

    @Test
    public void updateShelterRestDocTest() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        shelter.setId(1L);

        ShelterDto expected = new ShelterDto(shelter.getId(), shelter.getName(), shelter.getMaxCapacity(), shelter.getAnimals());
        when(shelternetService.updateShelter(1L, shelter)).thenReturn(expected);

        mockMvc.perform(put("/shelters/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Shelter("SHELTER1", 10))))
                .andExpect(status().isOk())
                .andDo(document("update-shelter", pathParameters(
                        parameterWithName("id").description("id of shelter to update")
                        ),
                        responseFields(fieldWithPath("id").description("Id of the shelter"),
                                fieldWithPath("name").description("Name of the shelter"),
                                fieldWithPath("capacity").description("Capacity of the shelter"),
                                fieldWithPath("animals").ignored()
                        ),
                        requestFields(
                                fieldWithPath("id").ignored(),
                                fieldWithPath("name").description("Name of the shelter"),
                                fieldWithPath("maxCapacity").description("Capacity of the shelter"),
                                fieldWithPath("animals").ignored()
                        )));
    }

    @Test
    public void deleteShelterRestDocTest() throws Exception {

        mockMvc.perform(delete("/shelters/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(document("delete-shelter", pathParameters(
                        parameterWithName("id").description("id of shelter to delete")
                )));

    }

    @Test
    public void acceptSurrenderedAnimals() throws Exception {
        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal.setId(1L);
        when(shelternetService.surrenderAnimal(1L, animal)).thenReturn(animal);

        mockMvc.perform(post("/shelters/1/animal/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animal)))
                .andExpect(status().isOk())
                .andDo(document("SurrenderAnimal",
                        responseFields(
                                fieldWithPath("id").description("Id of the Animal"),
                                fieldWithPath("name").description("Name of the Animal"),
                                fieldWithPath("species").description("Species of the Animal"),
                                fieldWithPath("birthDate").description("Birth Date of the Animal"),
                                fieldWithPath("sex").description("Sex of the Animal"),
                                fieldWithPath("color").description("Color of the Animal")),
                        requestFields(
                                fieldWithPath("id").ignored(),
                                fieldWithPath("name").description("Name of the Animal"),
                                fieldWithPath("species").description("Species of the Animal"),
                                fieldWithPath("birthDate").description("Birth Date of the Animal"),
                                fieldWithPath("sex").description("Sex of the Animal"),
                                fieldWithPath("color").description("Color of the Animal"))));
    }

    @Test
    public void getAnimalsByShelterId() throws Exception {
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("Cat", "AfricanCat", LocalDate.of(2021, 2, 1), "M", "black");
        animal1.setId(2L);
        animal2.setId(3L);

        List<Animal> expected = List.of(animal1, animal2);

        Shelter shelter = new Shelter("Shelter1", 50);
        shelter.setId(1L);
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);

        when(shelternetService.getAnimalsByShelterId(shelter.getId())).thenReturn(expected);

        mockMvc.perform(get("/shelters/animals/{shelterId}", shelter.getId()))
                .andExpect(status().isOk())
                .andDo(document("get-animals-by-shelter-id", pathParameters(
                        parameterWithName("shelterId").description("id of the shelter")
                        ),
                        responseFields(
                                fieldWithPath("[*].id").description("Id of the Animal"),
                                fieldWithPath("[*].name").description("Name of the Animal"),
                                fieldWithPath("[*].species").description("Species of the Animal"),
                                fieldWithPath("[*].birthDate").description("Birth Date of the Animal"),
                                fieldWithPath("[*].sex").description("Sex of the Animal"),
                                fieldWithPath("[*].color").description("Color of the Animal")
                        )));
    }
}
