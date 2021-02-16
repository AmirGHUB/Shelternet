package com.galvanize.shelternet.repository;

import com.galvanize.shelternet.model.Shelternet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelternetRepository extends JpaRepository<Shelternet, Long> {
}
