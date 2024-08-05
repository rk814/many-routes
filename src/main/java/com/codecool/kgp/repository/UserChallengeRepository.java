package com.codecool.kgp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, UUID> {
}
