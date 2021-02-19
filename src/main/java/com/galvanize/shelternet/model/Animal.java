package com.galvanize.shelternet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
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
    private Boolean onsite;
    private String notes;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Shelter shelter;
    private String status;

    public Animal(String name, String species, LocalDate birthDate, String sex, String color) {
        this.name = name;
        this.species = species;
        this.birthDate = birthDate;
        this.sex = sex;
        this.color = color;
        this.status="AVAILABLE";
        this.onsite = true;
    }
}
