package com.galvanize.shelternet.model;

import lombok.Value;

@Value
public class ShelterTrimmedDto {
    Long id;
    String name;
    Integer remainingCapacity;
    Integer maxCapacity;
}
