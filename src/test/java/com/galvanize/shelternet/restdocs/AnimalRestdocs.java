package com.galvanize.shelternet.restdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.controller.AnimalController;
import com.galvanize.shelternet.controller.ShelterController;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AnimalRepository;
import com.galvanize.shelternet.services.AnimalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnimalController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AnimalRestdocs {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnimalService animalService;
    @Test
    public void getAllAnimals() throws Exception {
        Animal animal1 = new Animal("Micro","Dog", LocalDate.now(),"M","Brown");
        Animal animal2 = new Animal("Sammy","Dog",LocalDate.now(),"M","Black");
        Animal animal3 = new Animal("Hunter","Dog",LocalDate.now(),"M","Brown");
        animal1.setId(1L);
        animal2.setId(2L);
        animal3.setId(3L);
        List<Animal> animals1 = List.of(animal1,animal2,animal3);
        when(animalService.getAllAnimals()).thenReturn(animals1);

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andDo(document("get-all-animals", responseFields(
                        fieldWithPath("[*].id").description("The ID of the animal."),
                        fieldWithPath("[*].name").description("The Name of the animal."),
                        fieldWithPath("[*].species").description("The Species of the animal."),
                        fieldWithPath("[*].birthDate").description("The BirthDate of the animal."),
                        fieldWithPath("[*].sex").description("The Sex of the animal."),
                        fieldWithPath("[*].color").description("The Color of the animal.")
                        )));

    }
}
