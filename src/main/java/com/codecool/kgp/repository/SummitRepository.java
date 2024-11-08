package com.codecool.kgp.repository;

import com.codecool.kgp.entity.Summit;
import com.codecool.kgp.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SummitRepository extends JpaRepository<Summit, UUID> {

    Optional<Summit> findByName(String name);

    List<Summit> findAllByStatus(Status status);
}
