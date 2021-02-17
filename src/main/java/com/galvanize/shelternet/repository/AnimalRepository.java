package com.galvanize.shelternet.repository;

import com.galvanize.shelternet.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
