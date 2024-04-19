package jurgenvrapi.S7L5progetto.controllers;

import jurgenvrapi.S7L5progetto.payloads.TokenResponse;
import jurgenvrapi.S7L5progetto.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jurgenvrapi.S7L5progetto.entities.Utente;
import jurgenvrapi.S7L5progetto.security.JWTTools;
import jurgenvrapi.S7L5progetto.services.UtenteService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UtenteController {

    private final UtenteService utenteService;
    private final JWTTools jwtTools;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        return new ResponseEntity<>("Utente registrato con successo", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody Utente utente) {
        Optional<Utente> existingUtente = utenteService.findUtenteByEmail(utente.getEmail());
        if (!existingUtente.isPresent() || !passwordEncoder.matches(utente.getPassword(), existingUtente.get().getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String token = jwtTools.createToken(existingUtente.get());

        return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
    }
}
