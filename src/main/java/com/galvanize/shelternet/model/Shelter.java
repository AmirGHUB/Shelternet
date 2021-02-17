package com.galvanize.shelternet.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private Integer capacity;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Animal> animals;

    public Shelter(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
        animals = new ArrayList<>();
    }

    public Shelter() {
        //empty constructor
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }
}
