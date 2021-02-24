package com.galvanize.shelternet.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterShelterDto {
    private String name;
    private Integer maxCapacity;
}
