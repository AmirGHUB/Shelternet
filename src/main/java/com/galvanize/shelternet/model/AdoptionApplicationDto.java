package com.galvanize.shelternet.model;

import lombok.*;

@Value
public class AdoptionApplicationDto {

    Long id;
    String name;
    String address;
    String phoneNumber;
    Long animalId;
    String status;


}
