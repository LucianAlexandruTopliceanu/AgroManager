package DomainModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pianta {
    private Integer id;
    private String tipo;
    private String varieta;
    private BigDecimal costo;
    private String note;
    private Integer fornitoreId;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;

    // Costruttori
    public Pianta() {}

    public Pianta(Integer id, String tipo, String varieta, BigDecimal costo, String note,
                  Integer fornitoreId, LocalDateTime dataCreazione, LocalDateTime dataAggiornamento) {
        this.id = id;
        this.tipo = tipo;
        this.varieta = varieta;
        this.costo = costo;
        this.note = note;
        this.fornitoreId = fornitoreId;
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

    public Integer getFornitoreId() { return fornitoreId; }
    public void setFornitoreId(Integer fornitoreId) { this.fornitoreId = fornitoreId; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(LocalDateTime dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }
}