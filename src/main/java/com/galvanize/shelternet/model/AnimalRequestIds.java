package com.galvanize.shelternet.model;

import lombok.Data;

import java.util.List;

@Data
public class AnimalRequestIds {
    private List<Long> animalIds;

    public AnimalRequestIds() {
        //empty constructor
    }

    public AnimalRequestIds(List<Long> list) {
        this.animalIds = list;
    }
}
