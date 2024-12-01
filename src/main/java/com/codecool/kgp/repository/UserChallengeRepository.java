package com.codecool.kgp.repository;

import com.codecool.kgp.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserChallengeRepository extends JpaRepository<UserChallenge, UUID> {

    List<UserChallenge> findAllByUserId(UUID id);

    @Query("SELECT uch FROM UserChallenge uch LEFT JOIN FETCH uch.userSummitsSet us LEFT JOIN FETCH uch.challenge" +
            " LEFT JOIN FETCH us.summit WHERE uch.user.id=:id")
    List<UserChallenge> findAllByUserIdWithAllRelationships(UUID id);
}
