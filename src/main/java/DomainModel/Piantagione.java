package DomainModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Piantagione {
    private Integer id;
    private Integer quantitaPianta;
    private LocalDate messaADimora;
    private Integer piantaId;
    private Integer zonaId;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;

    // Costruttori
    public Piantagione() {}

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
    }

    // Getter e Setter
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
}