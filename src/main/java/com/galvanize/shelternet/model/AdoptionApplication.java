package com.galvanize.shelternet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class AdoptionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    @OneToMany(cascade = CascadeType.MERGE)
    private List<Animal> animals;
    private String status;

    public AdoptionApplication(String name, String address, String phoneNumber, List<Animal> animals) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.animals = animals;
    }
}
