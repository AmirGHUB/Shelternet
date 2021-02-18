package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.model.ShelterDto;
import com.galvanize.shelternet.services.ShelternetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/shelters")
public class ShelterController {


    private ShelternetService shelternetService;

    public ShelterController(ShelternetService shelternetService) {
        this.shelternetService = shelternetService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterDto registerShelter(@RequestBody Shelter shelter) {
        return shelternetService.registerShelter(shelter);
    }

    @GetMapping
    public List<ShelterDto> getAllShelters(){
        return shelternetService.getAllShelters();
    }

    @GetMapping("/{id}")
    public ShelterDto getShelterDetails(@PathVariable Long id) {
        return shelternetService.getShelterDetails(id);
    }

    @PostMapping("/{id}/animal")
    public Animal surrenderAnimal(@PathVariable Long id, @RequestBody Animal animal) {
        return  shelternetService.surrenderAnimal(id, animal);
    }

    @PutMapping("/{id}")
    public ShelterDto updateShelter(@PathVariable Long id, @RequestBody Shelter shelterToUpdate) {
        return shelternetService.updateShelter(id, shelterToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteShelter(@PathVariable Long id) {
        shelternetService.delete(id);
    }


}
