package jurgenvrapi.S7L5progetto.controllers;

import jurgenvrapi.S7L5progetto.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jurgenvrapi.S7L5progetto.entities.Utente;
import jurgenvrapi.S7L5progetto.security.JWTTools;
import jurgenvrapi.S7L5progetto.services.UtenteService;

@RestController
@RequestMapping("/auth")
public class UtenteController {

    private final UtenteService utenteService;
    private final JWTTools jwtTools;

    @Autowired
    public UtenteController(UtenteService utenteService, JWTTools jwtTools) {
        this.utenteService = utenteService;
        this.jwtTools = jwtTools;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Utente utente) {
        if (UtenteRepository.existsByEmail(utente.getEmail())) {
            return new ResponseEntity<>("Esiste già un utente con questa email", HttpStatus.BAD_REQUEST);
        }

        utenteService.saveUtente(utente);
        String token = jwtTools.createToken(utente);

        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }
}
