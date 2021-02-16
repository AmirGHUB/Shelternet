package com.galvanize.shelternet.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Shelter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class HomeRestdocs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void homeTest() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andDo(document("ShelternetHome"));
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
                .andDo(document("GetAllShelters", responseFields(
                        fieldWithPath("[0].id").description("The ID of the shelter 1."),
                        fieldWithPath("[0].name").description("The name of the shelter 1."),
                        fieldWithPath("[0].capacity").description("The capacity of the shelter 1."),
                        fieldWithPath("[1].id").description("The ID of the shelter 2."),
                        fieldWithPath("[1].name").description("The name of the shelter 2."),
                        fieldWithPath("[1].capacity").description("The capacity of the shelter 2.")
                )));

    }
}
