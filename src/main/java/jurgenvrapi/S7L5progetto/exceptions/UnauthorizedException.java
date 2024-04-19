package jurgenvrapi.S7L5progetto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // Questa annotazione fa sì che Spring restituisca un codice di stato 401 quando questa eccezione viene lanciata
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}