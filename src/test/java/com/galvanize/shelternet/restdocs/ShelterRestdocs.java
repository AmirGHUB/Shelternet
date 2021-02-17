package com.galvanize.shelternet.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.services.ShelternetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
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
        when(shelternetService.registerShelter(shelter)).thenReturn(shelter);

        mockMvc.perform(post("/shelter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Shelter("SHELTER1", 10))))
                .andExpect(status().isCreated())
                .andDo(document("register-shelter", responseFields(
                        fieldWithPath("id").description("Id of the shelter"),
                        fieldWithPath("name").description("Name of the shelter"),
                        fieldWithPath("capacity").description("Capacity of the shelter")),
                        requestFields(
                                fieldWithPath("id").ignored(),
                                fieldWithPath("name").description("Name of the shelter"),
                                fieldWithPath("capacity").description("Capacity of the shelter"))));
    }


    @Test
    public void getAllShelters() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Shelter shelter2 = new Shelter("SHELTER2", 20);
        shelter.setId(1L);
        shelter2.setId(2L);
        when(shelternetService.getAllShelters()).thenReturn(List.of(shelter, shelter2));

        mockMvc
                .perform(get("/shelter"))
                .andExpect(status().isOk())
                .andDo(document("GetAllShelters", responseFields(
                        fieldWithPath("[*].id").description("The ID of the shelter."),
                        fieldWithPath("[*].name").description("The name of the shelter."),
                        fieldWithPath("[*].capacity").description("The capacity of the shelter.")
                )));
    }

    @Test
    public void getShelterDetails() throws Exception {

        Shelter shelter = new Shelter("SHELTER1", 10);

        shelter.setId(1L);

        when(shelternetService.getShelterDetails(shelter.getId())).thenReturn(Optional.of(shelter));

        mockMvc
                .perform(get("/shelter" + "/" + shelter.getId()))
                .andExpect(status().isOk())
                .andDo(document("GetShelterDetailsById", responseFields(
                        fieldWithPath("id").description("The ID of the shelter."),
                        fieldWithPath("name").description("The name of the shelter."),
                        fieldWithPath("capacity").description("The capacity of the shelter.")
                )));
    }

    @Test
    public void updateShelterRestDocTest() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        shelter.setId(1L);
        when(shelternetService.updateShelter(1L, shelter)).thenReturn(shelter);

        mockMvc.perform(put("/shelter/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Shelter("SHELTER1", 10))))
                .andExpect(status().isOk())
                .andDo(document("update-shelter", pathParameters(
                        parameterWithName("id").description("id of shelter to update")
                        ),
                        responseFields(fieldWithPath("id").description("Id of the shelter"),
                        fieldWithPath("name").description("Name of the shelter"),
                        fieldWithPath("capacity").description("Capacity of the shelter")),
                        requestFields(
                                fieldWithPath("id").ignored(),
                                fieldWithPath("name").description("Name of the shelter"),
                                fieldWithPath("capacity").description("Capacity of the shelter"))));
    }
    @Test
    public void deleteShelterRestDocTest() throws Exception {

        mockMvc.perform(delete("/shelter/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(document("delete-shelter", pathParameters(
                        parameterWithName("id").description("id of shelter to delete")
                        )));

    }

}
