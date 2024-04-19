package jurgenvrapi.S7L5progetto.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jurgenvrapi.S7L5progetto.entities.Utente;
import jurgenvrapi.S7L5progetto.repositories.UtenteRepository;

import java.util.Optional;

@Service
public class UtenteService {
    @Value("${jwt.secret}")
    private String secret;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UtenteService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Utente> findUtenteById(Long id) {
        return utenteRepository.findById(id);
    }

    public Utente saveUtente(Utente utente) {
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        return utenteRepository.save(utente);
    }

    public Optional<Utente> findUtenteByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    public Utente getUtenteFromToken(String token) {

        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .build().parseClaimsJws(token);

        String userId = jws.getBody().getSubject();

        Optional<Utente> optionalUtente = findUtenteById(Long.valueOf(userId));
        if (optionalUtente.isPresent()) {
            return optionalUtente.get();
        } else {
            throw new UsernameNotFoundException("User not found with id : " + userId);
        }
    }
}

