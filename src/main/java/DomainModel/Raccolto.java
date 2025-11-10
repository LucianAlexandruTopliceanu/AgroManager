package DomainModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Raccolto {
    private Integer id;
    private LocalDate dataRaccolto;
    private BigDecimal quantitaKg;
    private String note;
    private Integer piantagioneId;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;

    public Raccolto() {}

    public Raccolto(Integer id, LocalDate dataRaccolto, BigDecimal quantitaKg, String note,
                    Integer piantagione, LocalDateTime dataCreazione,
                    LocalDateTime dataAggiornamento) {
        this.id = id;
        this.dataRaccolto = dataRaccolto;
        this.quantitaKg = quantitaKg;
        this.note = note;
        this.piantagioneId = piantagione;
        this.dataCreazione = dataCreazione;
        this.dataAggiornamento = dataAggiornamento;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDataRaccolto() { return dataRaccolto; }
    public void setDataRaccolto(LocalDate dataRaccolto) { this.dataRaccolto = dataRaccolto; }

    public BigDecimal getQuantitaKg() { return quantitaKg; }
    public void setQuantitaKg(BigDecimal quantitaKg) { this.quantitaKg = quantitaKg; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getPiantagioneId() { return piantagioneId; }
    public void setPiantagioneId(Integer piantagioneId) { this.piantagioneId = piantagioneId; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(LocalDateTime dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }

}
