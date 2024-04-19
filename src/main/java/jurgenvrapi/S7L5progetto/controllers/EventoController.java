package jurgenvrapi.S7L5progetto.controllers;

import jurgenvrapi.S7L5progetto.entities.Evento;

import jurgenvrapi.S7L5progetto.entities.Utente;
import jurgenvrapi.S7L5progetto.enums.TipoUtente;
import jurgenvrapi.S7L5progetto.exceptions.UnauthorizedException;
import jurgenvrapi.S7L5progetto.payloads.EventoPayload;
import jurgenvrapi.S7L5progetto.security.JWTTools;
import jurgenvrapi.S7L5progetto.services.EventoService;
import jurgenvrapi.S7L5progetto.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/eventi")
public class EventoController {

    private final EventoService eventoService;
    private final JWTTools jwtTools;
    @Autowired
    private UtenteService utenteService;
    @Autowired
    public EventoController(EventoService eventoService, UtenteService utenteService, JWTTools jwtTools) {
        this.eventoService = eventoService;
        this.jwtTools = jwtTools;
    }

    @PostMapping("/crea")
    public ResponseEntity<EventoPayload> creaEvento(@RequestBody Evento evento, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            jwtTools.verifyToken(token);


            Utente utente = utenteService.getUtenteFromToken(token);

            if (utente.getTipoUtente() != TipoUtente.ORGANIZZATORE_DI_EVENTI) {
                return new ResponseEntity<EventoPayload>(new EventoPayload("Non hai i permessi per creare un evento!", null), HttpStatus.FORBIDDEN);
            }

            evento.setUtenteCreatore(utente);

            Evento createdEvento = eventoService.saveEvento(evento);
            return new ResponseEntity<>(new EventoPayload("Evento creato con successo", createdEvento), HttpStatus.CREATED);
        } catch (UnauthorizedException ex) {
            return new ResponseEntity<>(new EventoPayload("Problemi col token! Per favore effettua di nuovo il login!", null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new EventoPayload("Si è verificato un errore durante la creazione dell'evento", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Evento>> getAllEventi() {
        try {
            List<Evento> eventi = eventoService.getAllEventi();
            return new ResponseEntity<>(eventi, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoPayload> updateEvento(@PathVariable Long id, @RequestBody Evento updatedEvento, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            jwtTools.verifyToken(token);

            Utente utente = utenteService.getUtenteFromToken(token);

            Optional<Evento> optionalEvento = eventoService.findEventoById(id);

            if (optionalEvento.isPresent()) {
                Evento existingEvento = optionalEvento.get();

                if (!existingEvento.getUtenteCreatore().getId().equals(utente.getId())) {
                    return new ResponseEntity<>(new EventoPayload("Non hai i permessi per modificare questo evento!", null), HttpStatus.FORBIDDEN);
                }

                existingEvento.setNomeEvento(updatedEvento.getNomeEvento());
                existingEvento.setDescrizione(updatedEvento.getDescrizione());
                existingEvento.setLuogo(updatedEvento.getLuogo());
                existingEvento.setPosti(updatedEvento.getPosti());
                existingEvento.setDataEvento(updatedEvento.getDataEvento());

                Evento savedEvento = eventoService.saveEvento(existingEvento);
                return new ResponseEntity<>(new EventoPayload("Evento modificato con successo", savedEvento), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new EventoPayload("Evento non trovato", null), HttpStatus.NOT_FOUND);
            }
        } catch (UnauthorizedException ex) {
            return new ResponseEntity<>(new EventoPayload("Problemi col token! Per favore effettua di nuovo il login!", null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new EventoPayload("Si è verificato un errore durante la modifica dell'evento", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/book/{id}")
    public ResponseEntity<String> bookSeats(@PathVariable Long id, @RequestBody int seats, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            jwtTools.verifyToken(token);

            Optional<Evento> optionalEvento = eventoService.findEventoById(id);

            if (optionalEvento.isPresent()) {
                Evento evento = optionalEvento.get();

                if (evento.getPosti() < seats) {
                    return new ResponseEntity<>("Non ci sono abbastanza posti disponibili", HttpStatus.BAD_REQUEST);
                }

                evento.setPosti(evento.getPosti() - seats);
                eventoService.saveEvento(evento);

                return new ResponseEntity<>("Posti prenotati con successo", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Evento non trovato", HttpStatus.NOT_FOUND);
            }
        } catch (UnauthorizedException ex) {
            return new ResponseEntity<>("Problemi col token! Per favore effettua di nuovo il login!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Si è verificato un errore durante la prenotazione dei posti", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvento(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            jwtTools.verifyToken(token);

            Utente utente = utenteService.getUtenteFromToken(token);

            Optional<Evento> optionalEvento = eventoService.findEventoById(id);

            if (optionalEvento.isPresent()) {
                Evento existingEvento = optionalEvento.get();

                if (!existingEvento.getUtenteCreatore().getId().equals(utente.getId())) {
                    return new ResponseEntity<>("Non hai i permessi per eliminare questo evento!", HttpStatus.FORBIDDEN);
                }

                eventoService.deleteEvento(id);

                return new ResponseEntity<>("Evento eliminato con successo", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Evento non trovato", HttpStatus.NOT_FOUND);
            }
        } catch (UnauthorizedException ex) {
            return new ResponseEntity<>("Problemi col token! Per favore effettua di nuovo il login!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Si è verificato un errore durante l'eliminazione dell'evento", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
