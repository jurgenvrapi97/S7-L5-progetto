package jurgenvrapi.S7L5progetto.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nomeEvento;
    private String descrizione;
    private String luogo;
    private int posti;
    private Date dataEvento;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utenteCreatore;


}
