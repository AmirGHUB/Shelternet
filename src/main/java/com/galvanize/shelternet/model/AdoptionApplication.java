package com.galvanize.shelternet.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private Long animalId;
    private String status;

    public AdoptionApplication(String name, String address, String phoneNumber, Long animalId) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.animalId = animalId;
    }
}
