package com.galvanize.shelternet.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private Integer capacity;

    public Shelter(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public Shelter() {
        //empty constructor
    }
}
