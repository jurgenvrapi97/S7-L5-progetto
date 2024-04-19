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

}
