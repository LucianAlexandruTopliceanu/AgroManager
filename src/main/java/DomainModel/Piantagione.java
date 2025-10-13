package DomainModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Piantagione {
    private Integer id;
    private Integer quantitaPianta;
    private LocalDate messaADimora;
    private Integer piantaId;
    private Integer zonaId;
    private Integer idStatoPiantagione; // NUOVO: FK verso Stato_Piantagione
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;


    private StatoPiantagione statoPiantagione;

    public Piantagione() {
        this.idStatoPiantagione = 1; // Default: ATTIVA
    }

    public Piantagione(Integer id, Integer quantitaPianta, LocalDate messaADimora,
                       Integer pianta, Integer zona, LocalDateTime dataCreazione,
                       LocalDateTime dataAggiornamento) {
        this.id = id;
        this.quantitaPianta = quantitaPianta;
        this.messaADimora = messaADimora;
        this.piantaId = pianta;
        this.zonaId = zona;
        this.dataCreazione = dataCreazione;
        this.dataAggiornamento = dataAggiornamento;
        this.idStatoPiantagione = 1; // Default: ATTIVA
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuantitaPianta() { return quantitaPianta; }
    public void setQuantitaPianta(Integer quantitaPianta) { this.quantitaPianta = quantitaPianta; }

    public LocalDate getMessaADimora() { return messaADimora; }
    public void setMessaADimora(LocalDate messaADimora) { this.messaADimora = messaADimora; }

    public Integer getPiantaId() { return piantaId; }
    public void setPiantaId(Integer pianta) { this.piantaId = pianta; }

    public Integer getZonaId() { return zonaId; }
    public void setZonaId(Integer zona) { this.zonaId = zona; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(LocalDateTime dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }

    public Integer getIdStatoPiantagione() {
        return idStatoPiantagione;
    }
    public void setIdStatoPiantagione(Integer idStatoPiantagione) {
        this.idStatoPiantagione = idStatoPiantagione;
    }

    public StatoPiantagione getStatoPiantagione() {
        return statoPiantagione;
    }
    public void setStatoPiantagione(StatoPiantagione statoPiantagione) {
        this.statoPiantagione = statoPiantagione;
        if (statoPiantagione != null) {
            this.idStatoPiantagione = statoPiantagione.getId();
        }
    }

}