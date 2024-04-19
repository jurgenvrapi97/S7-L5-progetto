package jurgenvrapi.S7L5progetto.services;

import jurgenvrapi.S7L5progetto.entities.Evento;
import jurgenvrapi.S7L5progetto.repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento saveEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<Evento> findEventoById(Long id) {
        return eventoRepository.findById(id);
    }

    public void deleteEvento(Long id) {
        eventoRepository.deleteById(id);
    }

    public List<Evento> getAllEventi() {
        return eventoRepository.findAll();
    }
}
