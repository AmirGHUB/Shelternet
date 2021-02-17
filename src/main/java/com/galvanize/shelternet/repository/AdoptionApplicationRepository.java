package com.galvanize.shelternet.repository;

import com.galvanize.shelternet.model.AdoptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
}
