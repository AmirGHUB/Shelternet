package com.galvanize.shelternet.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Shelternet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Shelter> shelters = new ArrayList<>();


    public void addShelter(Shelter shelter) {
        this.shelters.add(shelter);
    }
}
