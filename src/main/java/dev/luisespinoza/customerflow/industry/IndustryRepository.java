package dev.luisespinoza.customerflow.industry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndustryRepository extends JpaRepository<Industry, Long> {
    Optional<Industry> findByName(String industryName);
}
