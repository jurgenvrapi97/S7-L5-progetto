package jurgenvrapi.S7L5progetto.entities;

import jakarta.persistence.*;
import jurgenvrapi.S7L5progetto.enums.TipoUtente;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;
    private String cognome;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private TipoUtente tipoUtente;

    @OneToMany(mappedBy = "utenteCreatore")
    private Set<Evento> eventiCreati;
}
