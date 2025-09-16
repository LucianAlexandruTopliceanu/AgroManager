package DomainModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Raccolto {
    private Integer id;
    private LocalDate dataRaccolto;
    private BigDecimal quantitaKg;
    private String note;
    private Piantagione piantagione;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;

    // Costruttori
    public Raccolto() {}

    public Raccolto(Integer id, LocalDate dataRaccolto, BigDecimal quantitaKg, String note,
                    Piantagione piantagione, LocalDateTime dataCreazione,
                    LocalDateTime dataAggiornamento) {
        this.id = id;
        this.dataRaccolto = dataRaccolto;
        this.quantitaKg = quantitaKg;
        this.note = note;
        this.piantagione = piantagione;
        this.dataCreazione = dataCreazione;
        this.dataAggiornamento = dataAggiornamento;
    }

    // Getter e Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDataRaccolto() { return dataRaccolto; }
    public void setDataRaccolto(LocalDate dataRaccolto) { this.dataRaccolto = dataRaccolto; }

    public BigDecimal getQuantitaKg() { return quantitaKg; }
    public void setQuantitaKg(BigDecimal quantitaKg) { this.quantitaKg = quantitaKg; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Piantagione getPiantagione() { return piantagione; }
    public void setPiantagione(Piantagione piantagione) { this.piantagione = piantagione; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(LocalDateTime dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }
}