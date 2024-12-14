package pl.manyroutes.repository;

import pl.manyroutes.entity.Summit;
import pl.manyroutes.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SummitRepository extends JpaRepository<Summit, UUID> {

    Optional<Summit> findByName(String name);

    List<Summit> findAllByStatus(Status status);

    @Query("SELECT s FROM Summit s LEFT JOIN FETCH s.challengesSet ch WHERE s.status=:status")
    List<Summit> findAllByStatusWithChallenges(Status status);
}
