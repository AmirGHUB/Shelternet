package com.galvanize.shelternet.repository;

import com.galvanize.shelternet.model.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
}
