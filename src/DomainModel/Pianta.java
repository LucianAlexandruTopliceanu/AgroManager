package DomainModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pianta {
    private Integer id;
    private String tipo;
    private String varieta;
    private BigDecimal costo;
    private String note;
    private Fornitore fornitore;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;

    // Costruttori
    public Pianta() {}

    public Pianta(Integer id, String tipo, String varieta, BigDecimal costo, String note,
                  Fornitore fornitore, LocalDateTime dataCreazione, LocalDateTime dataAggiornamento) {
        this.id = id;
        this.tipo = tipo;
        this.varieta = varieta;
        this.costo = costo;
        this.note = note;
        this.fornitore = fornitore;
        this.dataCreazione = dataCreazione;
        this.dataAggiornamento = dataAggiornamento;
    }

    // Getter e Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getVarieta() { return varieta; }
    public void setVarieta(String varieta) { this.varieta = varieta; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Fornitore getFornitore() { return fornitore; }
    public void setFornitore(Fornitore fornitore) { this.fornitore = fornitore; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(LocalDateTime dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }
}