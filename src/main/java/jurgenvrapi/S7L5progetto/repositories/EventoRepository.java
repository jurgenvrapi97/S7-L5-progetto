package jurgenvrapi.S7L5progetto.repositories;

import jurgenvrapi.S7L5progetto.entities.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}
