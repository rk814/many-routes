package pl.manyroutes.repository;

import pl.manyroutes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByLogin(String login);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userChallengesSet uc LEFT JOIN FETCH uc.challenge")
    List<User> findAllWithChallenges();
}
