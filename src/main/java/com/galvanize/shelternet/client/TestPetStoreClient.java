package com.galvanize.shelternet.client;

import com.galvanize.shelternet.model.AnimalRequestIds;
import com.galvanize.shelternet.model.AnimalReturnFromPetStoreDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("test")
public class TestPetStoreClient implements PetStoreClient {

    @Override
    public List<AnimalReturnFromPetStoreDto> returnRequest(AnimalRequestIds animalRequestIds) {
        return animalRequestIds.getAnimalIds().stream()
                .map(id -> new AnimalReturnFromPetStoreDto(id, "NOTE"))
                .collect(Collectors.toList());
    }
}
