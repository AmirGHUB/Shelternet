package com.galvanize.shelternet.client;

import com.galvanize.shelternet.model.AnimalRequestIds;
import com.galvanize.shelternet.model.AnimalReturnFromPetStoreDto;

import java.util.List;

public interface PetStoreClient {
    List<AnimalReturnFromPetStoreDto> returnRequest(AnimalRequestIds animalRequestIds);
}
