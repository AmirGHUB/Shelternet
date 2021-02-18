package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.services.AdoptionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class AdoptionApplicationController {

    private AdoptionApplicationService adoptionApplicationService;

    public AdoptionApplicationController(AdoptionApplicationService adoptionApplicationService) {
        this.adoptionApplicationService = adoptionApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AdoptionApplication> submitApplication(@RequestBody AdoptionApplication adoptionApplication) {
        return adoptionApplicationService.submitAdoptionApplication(adoptionApplication)
                .map(application -> new ResponseEntity<>(application, HttpStatus.CREATED))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public List<AdoptionApplication> getAllApplications() {
        return adoptionApplicationService.getAllApplications();
    }
}
