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
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Integer maxCapacity;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Animal> animals;

    public Shelter(String name, Integer maxCapacity) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        animals = new ArrayList<>();
    }

    public Shelter() {
        //empty constructor
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }
}
