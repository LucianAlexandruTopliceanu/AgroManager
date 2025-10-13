package DomainModel;

import java.time.LocalDateTime;

public class Zona {
    private Integer id;
    private String nome;
    private Double dimensione;
    private String tipoTerreno;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;

    public Zona() {}

    public Zona(Integer id, String nome, Double dimensione, String tipoTerreno,
                LocalDateTime dataCreazione, LocalDateTime dataAggiornamento) {
        this.id = id;
        this.nome = nome;
        this.dimensione = dimensione;
        this.tipoTerreno = tipoTerreno;
        this.dataCreazione = dataCreazione;
        this.dataAggiornamento = dataAggiornamento;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getDimensione() { return dimensione; }
    public void setDimensione(Double dimensione) { this.dimensione = dimensione; }

    public String getTipoTerreno() { return tipoTerreno; }
    public void setTipoTerreno(String tipoTerreno) { this.tipoTerreno = tipoTerreno; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(LocalDateTime dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }

    @Override
    public String toString() {
        return nome + " (" + dimensione + "ha, " + tipoTerreno + ")";
    }
}