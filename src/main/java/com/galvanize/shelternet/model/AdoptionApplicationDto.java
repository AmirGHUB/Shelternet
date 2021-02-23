package com.galvanize.shelternet.model;

import lombok.*;
import java.util.List;

@Value
public class AdoptionApplicationDto {

    Long id;
    String name;
    String address;
    String phoneNumber;
    List<Long> animalIds;
    String status;


}
