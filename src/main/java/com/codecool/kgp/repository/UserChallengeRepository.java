package com.codecool.kgp.repository;

import com.codecool.kgp.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, UUID> {

    List<UserChallenge> findAllByUserId(UUID id);
}
