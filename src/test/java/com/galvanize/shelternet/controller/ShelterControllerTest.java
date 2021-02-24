package com.galvanize.shelternet.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.AnimalTransfer;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.repository.AnimalRepository;
import com.galvanize.shelternet.repository.ShelterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @BeforeEach
    public void setUp() {
        this.shelterRepository.deleteAll();
        this.animalRepository.deleteAll();
    }

    @Test
    public void registerShelterTest() throws Exception {
        String shelter = "{\n" +
                "    \"name\":\"SHELTER6\",\n" +
                "    \"maxCapacity\":5\n" +
                "}";

        mockMvc.perform(post("/shelters")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(shelter)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SHELTER6"))
                .andExpect(jsonPath("$.maxCapacity").value(5));
    }

    @Test
    public void registerShelterTest_withoutNameAndCapacity() {
        Shelter shelter = new Shelter(null, 0);

        assertThrows(NestedServletException.class,
                () -> mockMvc.perform(post("/shelters")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                        .content(objectMapper.writeValueAsString(shelter))
                        .contentType(MediaType.APPLICATION_JSON)));
    }

    @Test
    @Transactional(value = Transactional.TxType.NEVER)

    public void registerShelter_withDuplicateName() {
        try {
            Shelter shelter = new Shelter("SHELTER1", 10);

            mockMvc.perform(post("/shelters")
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                    .content(objectMapper.writeValueAsString(shelter))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("SHELTER1"))
                    .andExpect(jsonPath("$.maxCapacity").value(10));

            assertThrows(NestedServletException.class,
                    () -> mockMvc.perform(post("/shelters")
                            .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                            .content(objectMapper.writeValueAsString(shelter))
                            .contentType(MediaType.APPLICATION_JSON)));
        } catch (Exception e) {
            fail();
        } finally {
            shelterRepository.deleteAll();
        }
    }

    @Test
    public void getAllShelters() throws Exception {
        Shelter shelter = shelterRepository.save(new Shelter("SHELTER1", 10));
        Shelter shelter2 = shelterRepository.save(new Shelter("SHELTER2", 20));

        mockMvc
                .perform(get("/shelters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("[0].id").value(shelter.getId()))
                .andExpect(jsonPath("[0].name").value("SHELTER1"))
                .andExpect(jsonPath("[0].maxCapacity").value(10))
                .andExpect(jsonPath("[0].remainingCapacity").value(10))
                .andExpect(jsonPath("[1].id").value(shelter2.getId()))
                .andExpect(jsonPath("[1].name").value("SHELTER2"))
                .andExpect(jsonPath("[1].maxCapacity").value(20))
                .andExpect(jsonPath("[0].remainingCapacity").value(10));

    }

    @Test
    public void getShelterDetails() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("hob", "wildcat", LocalDate.of(2010, 5, 2), "F", "white");
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);

        shelter = shelterRepository.save(shelter);

        mockMvc
                .perform(get("/shelters" + "/" + shelter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SHELTER1"))
                .andExpect(jsonPath("$.remainingCapacity").value(8));

    }


    @Test
    public void acceptSurrenderedAnimals() throws Exception {
        Shelter shelter = new Shelter("SHELTER1", 10);

        MvcResult result = mockMvc.perform(post("/shelters")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(objectMapper.writeValueAsString(shelter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        Shelter shelterResult = objectMapper.readValue(jsonResult, Shelter.class);

        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");

        mockMvc.perform(post("/shelters/" + shelterResult.getId() + "/animal/")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(animal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Dog"))
                .andExpect(jsonPath("$.species").value("Dalmention"))
                .andExpect(jsonPath("$.birthDate").value("2009-04-01"))
                .andExpect(jsonPath("$.sex").value("M"))
                .andExpect(jsonPath("$.color").value("black"));

        Animal retrievedAnimal = animalRepository.findAll().get(0);

        assertEquals("SHELTER1", retrievedAnimal.getShelter().getName());
        assertEquals("AVAILABLE", retrievedAnimal.getStatus());

        mockMvc
                .perform(get("/shelters/" + shelterResult.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingCapacity").value(9));
    }

    @Test
    public void updateShelter() throws Exception {
        Shelter existingShelter = new Shelter("Original Shelter", 20);
        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        existingShelter.addAnimal(animal);
        existingShelter = shelterRepository.save(existingShelter);
        Shelter shelterToUpdate = new Shelter("Updated Shelter", 10);
        String shelterString = objectMapper.writeValueAsString(shelterToUpdate);

        mockMvc.perform(put("/shelters/" + existingShelter.getId())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .content(shelterString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingShelter.getId()))
                .andExpect(jsonPath("$.name").value("Updated Shelter"))
                .andExpect(jsonPath("$.remainingCapacity").value(9))
                .andExpect(jsonPath("$.animals.[0].name").value("Dog"));
    }

    @Test
    public void deleteShelterById() throws Exception {
        Shelter existingShelter = shelterRepository.save(new Shelter("Original Shelter", 20));
        mockMvc.perform(delete("/shelters/" + existingShelter.getId())
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1")))
                .andExpect(status().isOk());
        assertEquals(0, shelterRepository.findAll().size());
    }

    @Test
    public void transferAnimal_succesfullyTransfersAnimal() throws Exception {
        Shelter shelter1 = new Shelter("Shelter A", 20);
        Animal animal = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        shelter1.addAnimal(animal);
        shelter1 = shelterRepository.save(shelter1);
        Shelter shelter2 = shelterRepository.save(new Shelter("Shelter B", 20));

        AnimalTransfer animalTransfer = new AnimalTransfer(shelter1.getId(), shelter2.getId(), shelter1.getAnimals().get(0).getId());
        String jsonString = objectMapper.writeValueAsString(animalTransfer);

        mockMvc.perform(put("/shelters/transfer-animal")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk());

        assertEquals(0, shelterRepository.findById(shelter1.getId()).orElseThrow().getAnimals().size());
        assertEquals(1, shelterRepository.findById(shelter2.getId()).orElseThrow().getAnimals().size());
    }

    @Test
    public void transferAnimal_returnsBadRequest() throws Exception {
        Shelter shelter1 = new Shelter("Shelter A", 20);
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        shelter1.addAnimal(animal1);
        shelter1 = shelterRepository.save(shelter1);
        Shelter shelter2 = new Shelter("Shelter B", 1);
        Animal animal2 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        shelter2.addAnimal(animal2);
        shelter2 = shelterRepository.save(shelter2);

        AnimalTransfer animalTransfer = new AnimalTransfer(shelter1.getId(), shelter2.getId(), shelter1.getAnimals().get(0).getId());
        String jsonString = objectMapper.writeValueAsString(animalTransfer);

        mockMvc.perform(put("/shelters/transfer-animal")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "shelterPass1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isBadRequest());

        assertEquals(1, shelterRepository.findById(shelter1.getId()).orElseThrow().getAnimals().size());
        assertEquals(1, shelterRepository.findById(shelter2.getId()).orElseThrow().getAnimals().size());
    }

    @Test
    public void getAnimalsByShelterId() throws Exception {
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("Cat", "AfricanCat", LocalDate.of(2021, 2, 1), "M", "black");
        List<Animal> expected = List.of(animal1, animal2);
        Shelter shelter = new Shelter("Shelter1", 50);
        shelter.addAnimal(animal1);
        shelter.addAnimal(animal2);
        shelter = shelterRepository.save(shelter);

        String expectedString = objectMapper.writeValueAsString(expected);
        mockMvc.perform(get("/shelters/" + shelter.getId() + "/animals"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedString));
    }

}

