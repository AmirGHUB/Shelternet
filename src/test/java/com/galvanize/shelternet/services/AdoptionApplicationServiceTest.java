package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.AdoptionApplication;
import com.galvanize.shelternet.model.AdoptionApplicationDto;
import com.galvanize.shelternet.model.Animal;
import com.galvanize.shelternet.repository.AdoptionApplicationRepository;
import com.galvanize.shelternet.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        Animal animal2 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal1.setId(1L);
        animal2.setId(2L);
        animal2.setStatus("ADOPTION_PENDING");
        AdoptionApplicationDto adoptionApplicationDto =
                new AdoptionApplicationDto(1L,"JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L, "PENDING");
        AdoptionApplication adoptionApplication =
                new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        adoptionApplication.setId(1L);
        when(adoptionApplicationRepository.save(any())).thenReturn(adoptionApplication);
        adoptionApplication.setId(1L);
        adoptionApplication.setStatus("PENDING");
        when(adoptionApplicationRepository.save(any())).thenReturn(adoptionApplication);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal1));
        when(animalRepository.save(animal2)).thenReturn(null);
        AdoptionApplicationDto result = adoptionApplicationService.submitAdoptionApplication(adoptionApplicationDto);

        verify(adoptionApplicationRepository, times(1)).save(any());
        verifyNoMoreInteractions(adoptionApplicationRepository);
        verifyNoMoreInteractions(animalRepository);

        assertEquals(adoptionApplicationDto, result);
    }


    @Test
    public void submitAdoptionApplication_returnsNullIfAnimalNotPresent() {

        AdoptionApplicationDto adoptionApplicationDto = new AdoptionApplicationDto(1L,"JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L,"PENDING");
        when(animalRepository.findById(1L)).thenReturn(Optional.ofNullable(null));
        AdoptionApplicationDto result = adoptionApplicationService.submitAdoptionApplication(adoptionApplicationDto);

        verifyNoMoreInteractions(animalRepository);

        assertEquals(null, result);
    }

    @Test
    public void submitAdoptionApplication_returnsNullIfAnimalNotAvailable() {
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal1.setId(1L);
        animal1.setStatus("ADOPTION_PENDING");
        AdoptionApplicationDto adoptionApplicationDto = new AdoptionApplicationDto(1L, "JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L,"PENDING");
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal1));
        AdoptionApplicationDto result = adoptionApplicationService.submitAdoptionApplication(adoptionApplicationDto);

        verifyNoMoreInteractions(animalRepository);

        assertEquals(null, result);
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

    @Test
    public void updateStatus_setsStatusToApproved_andRemovesAnimalFromShelter() {
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal1.setId(1L);
        animal1.setStatus("ADOPTION_PENDING");
        Animal animal2 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal2.setId(1L);
        animal2.setStatus("ADOPTED");

        AdoptionApplication adoptionApplication1 = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        adoptionApplication1.setId(1L);
        AdoptionApplication adoptionApplication2 = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        adoptionApplication2.setId(1L);
        adoptionApplication2.setStatus("APPROVED");

        when(adoptionApplicationRepository.findById(1L)).thenReturn(Optional.of(adoptionApplication1));
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal1));
        when(animalRepository.save(animal2)).thenReturn(null);
        when(adoptionApplicationRepository.save(adoptionApplication2)).thenReturn(null);

        adoptionApplicationService.updateStatus(1L, true);

        verifyNoMoreInteractions(adoptionApplicationRepository);
        verifyNoMoreInteractions(animalRepository);
    }

    @Test
    public void updateStatus_setsStatusToRejected_andSetsAnimalStatusToAvailable() {
        Animal animal1 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal1.setId(1L);
        animal1.setStatus("ADOPTION_PENDING");
        Animal animal2 = new Animal("Dog", "Dalmention", LocalDate.of(2009, 4, 1), "M", "black");
        animal2.setId(1L);
        animal2.setStatus("AVAILABLE");

        AdoptionApplication adoptionApplication1 = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        adoptionApplication1.setId(1L);
        AdoptionApplication adoptionApplication2 = new AdoptionApplication("JOHN", "5131 W Thunderbird Rd.", "602-444-4444", 1L);
        adoptionApplication2.setId(1L);
        adoptionApplication2.setStatus("REJECTED");

        when(adoptionApplicationRepository.findById(1L)).thenReturn(Optional.of(adoptionApplication1));
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal1));
        when(animalRepository.save(animal2)).thenReturn(null);
        when(adoptionApplicationRepository.save(adoptionApplication2)).thenReturn(null);

        adoptionApplicationService.updateStatus(1L, false);

        verifyNoMoreInteractions(adoptionApplicationRepository);
        verifyNoMoreInteractions(animalRepository);
    }

}
