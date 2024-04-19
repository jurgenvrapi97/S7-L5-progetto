package jurgenvrapi.S7L5progetto.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jurgenvrapi.S7L5progetto.entities.Utente;
import jurgenvrapi.S7L5progetto.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



import java.util.Date;

@Component
public class JWTTools {

    @Value("${jwt.secret}")
    private String secret;

    public String createToken(Utente utente){
        return Jwts.builder()
                .setSubject(String.valueOf(utente.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public void verifyToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build().parse(token);

        } catch (Exception ex) {
            throw new UnauthorizedException("Problemi col token! Per favore effettua di nuovo il login!");

        }

    }

}
