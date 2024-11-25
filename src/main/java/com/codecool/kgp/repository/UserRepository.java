package com.codecool.kgp.repository;

import com.codecool.kgp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userChallenges uc LEFT JOIN FETCH uc.challenge")
    List<User> findAllWithChallenges();
}
