package com.codecool.kgp.repository;

import com.codecool.kgp.entity.UserSummit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSummitRepository extends JpaRepository<UserSummit, UUID> {
}
