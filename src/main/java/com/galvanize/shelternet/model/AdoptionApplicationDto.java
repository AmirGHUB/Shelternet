package com.galvanize.shelternet.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Value
public class AdoptionApplicationDto {

    Long id;
    String name;
    String address;
    String phoneNumber;
    Long animalId;
    String status;


}
