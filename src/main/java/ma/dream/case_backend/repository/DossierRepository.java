package ma.dream.case_backend.repository;

import ma.dream.case_backend.model.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierRepository extends JpaRepository<Dossier, Long> {



}
