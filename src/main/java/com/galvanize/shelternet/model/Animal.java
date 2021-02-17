package com.galvanize.shelternet.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = "id")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String species;
    private LocalDate birthDate;
    private String sex;
    private String color;

    public Animal(String name, String species, LocalDate birthDate, String sex, String color) {
        this.name = name;
        this.species = species;
        this.birthDate = birthDate;
        this.sex = sex;
        this.color = color;
    }
}
