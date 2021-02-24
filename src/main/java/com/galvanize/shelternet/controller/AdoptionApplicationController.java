package com.galvanize.shelternet.controller;

import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.AdoptionApplicationDto;
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
    public ResponseEntity<AdoptionApplicationDto> submitApplication(@RequestBody AdoptionApplicationDto adoptionApplicationDto) {
        AdoptionApplicationDto updatedApplication = adoptionApplicationService.submitAdoptionApplication(adoptionApplicationDto);
        return updatedApplication == null
                ? new ResponseEntity<>(HttpStatus.BAD_REQUEST) : new ResponseEntity<>(updatedApplication, HttpStatus.CREATED);
    }

    @GetMapping
    public List<AdoptionApplication> getAllApplications() {
        return adoptionApplicationService.getAllApplications();
    }

    @PutMapping("{id}/update-status")
    public void updateStatus(@PathVariable Long id, @RequestParam boolean isApproved) {
        adoptionApplicationService.updateStatus(id, isApproved);
    }
}
