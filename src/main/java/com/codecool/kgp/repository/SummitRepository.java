package com.codecool.kgp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SummitRepository extends JpaRepository<Summit, UUID> {
}
