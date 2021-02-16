package com.galvanize.shelternet.services;

import com.galvanize.shelternet.model.Shelter;
import com.galvanize.shelternet.model.Shelternet;
import com.galvanize.shelternet.repository.ShelternetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelternetServiceTest {

    @Mock
    private ShelternetRepository shelternetRepository;

    @InjectMocks
    private ShelternetService shelternetService;

    @Test
    public void registerShelterTest() {
        Shelter shelter = new Shelter("SHELTER1", 10);
        Shelternet shelternet = new Shelternet();
        shelternet.setId(1L);

        when(shelternetRepository.getOne(any())).thenReturn(shelternet);
        when(shelternetRepository.save(any())).thenReturn(shelternet);

        Shelter shelterAdded = shelternetService.registerShelter(shelter, shelternet.getId());

        verify(shelternetRepository, times(1)).save(any());

        assertEquals(shelter, shelterAdded);
    }
}
