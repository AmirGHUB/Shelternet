package com.galvanize.shelternet.client;

import com.galvanize.shelternet.model.AnimalRequestIds;
import com.galvanize.shelternet.model.AnimalReturnFromPetStoreDto;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("!test")
public class PetStoreClientImpl implements PetStoreClient {

    @Override
    public List<AnimalReturnFromPetStoreDto> returnRequest(AnimalRequestIds animalRequestIds) {
        String petStoreUrl = "https://pet-store-api-staging.herokuapp.com/animals/return-request";
        RestTemplate restTemplate = new RestTemplate();

        List<String> animalIds = animalRequestIds.getAnimalIds().stream().map(Object::toString).collect(Collectors.toList());
        HttpEntity<List<String>> request = new HttpEntity<>(animalIds);
        ParameterizedTypeReference<List<AnimalReturnFromPetStoreDto>> responseType = new ParameterizedTypeReference<>() {
        };
        return restTemplate.exchange(petStoreUrl, HttpMethod.DELETE, request, responseType).getBody();
    }

}
