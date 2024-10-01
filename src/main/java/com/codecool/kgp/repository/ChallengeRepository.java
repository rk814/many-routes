package com.codecool.kgp.repository;

import com.codecool.kgp.entity.Challenge;
import com.codecool.kgp.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {

    List<Challenge> findAllByStatus(Status status);

    @Query("SELECT ch FROM Challenge ch JOIN FETCH ch.summitList WHERE ch.status=:status")
    List<Challenge> findAllByStatusWithSummits(Status status);
}
