package com.galvanize.shelternet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class AnimalDto {
    private Long id;
    private String name;
    private String species;
    private LocalDate birthDate;
    private String sex;
    private String color;
    private String notes;
}
