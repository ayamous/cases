package ma.dream.case_backend.repository;

import ma.dream.case_backend.model.PresenceJour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresenceJourRepository extends JpaRepository<PresenceJour, Long> {
}
