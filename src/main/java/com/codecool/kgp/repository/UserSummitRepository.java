package com.codecool.kgp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSummitRepository extends JpaRepository<UserSummit, UUID> {
}
