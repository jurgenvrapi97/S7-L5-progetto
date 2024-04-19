package jurgenvrapi.S7L5progetto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import jurgenvrapi.S7L5progetto.entities.Utente;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    static boolean existsByEmail(String email) {
        return false;
    }


}