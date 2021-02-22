package com.galvanize.shelternet.model;

public class AnimalMapper {

    public static AnimalDto mapToDto(Animal animalEntity) {
        return new AnimalDto(
                animalEntity.getId(),
                animalEntity.getName(),
                animalEntity.getSpecies(),
                animalEntity.getBirthDate(),
                animalEntity.getSex(),
                animalEntity.getColor(),
                animalEntity.getNotes());
    }

    public static Animal mapToEntity(AnimalDto animalDto) {
        return new Animal(
                animalDto.getName(),
                animalDto.getSpecies(),
                animalDto.getBirthDate(),
                animalDto.getSex(),
                animalDto.getColor());
    }
}
