package com.galvanize.shelternet.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShelterDto {
    private Long id;
    private String name;
    private Integer remainingCapacity;
    private Integer maxCapacity;
    private List<Animal> animals;

}
