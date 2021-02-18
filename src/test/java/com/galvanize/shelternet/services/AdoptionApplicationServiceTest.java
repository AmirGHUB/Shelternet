package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AdoptionApplicationRepository;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdoptionApplicationServiceTest {

    @Mock
    private AdoptionApplicationRepository adoptionApplicationRepository;

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private AdoptionApplicationService adoptionApplicationService;

    @Test
    public void submitAdoptionApplication() {
        AdoptionApplication adoptionApplication = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        when(adoptionApplicationRepository.save(adoptionApplication)).thenReturn(adoptionApplication);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(new Animal()));
        Optional<AdoptionApplication> result = adoptionApplicationService.submitAdoptionApplication(adoptionApplication);

        verify(adoptionApplicationRepository, times(1)).save(adoptionApplication);
        verifyNoMoreInteractions(adoptionApplicationRepository);

        assertEquals(Optional.of(adoptionApplication), result);
    }

    @Test
    public void submitAdoptionApplication_ForAnAnimalThatDoesntExists() {
        AdoptionApplication adoptionApplication = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<AdoptionApplication> result = adoptionApplicationService.submitAdoptionApplication(adoptionApplication);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void getAllApplications_callsFindAllOnRepository() {
        AdoptionApplication adoptionApplication1 = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        AdoptionApplication adoptionApplication2 = new AdoptionApplication("Mark", "another address", "876-990-7661", 4L);
        AdoptionApplication adoptionApplication3 = new AdoptionApplication("Jane", "yet another address", "145-640-9900", 5L);
        List<AdoptionApplication> applications = List.of(adoptionApplication1, adoptionApplication2, adoptionApplication3);

        when(adoptionApplicationRepository.findAll()).thenReturn(applications);

        List<AdoptionApplication> result = adoptionApplicationService.getAllApplications();

        assertEquals(applications, result);
        verify(adoptionApplicationRepository, times(1)).findAll();
        verifyNoMoreInteractions(adoptionApplicationRepository);
    }


}
