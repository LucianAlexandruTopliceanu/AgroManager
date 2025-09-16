package DomainModel;

import java.time.LocalDateTime;

public class Fornitore {
    private Integer id;
    private String nome;
    private String indirizzo;
    private String numeroTelefono;
    private String email;
    private String partitaIva;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;

    // Costruttori
    public Fornitore() {}

    public Fornitore(Integer id, String nome, String indirizzo, String numeroTelefono,
                     String email, String partitaIva, LocalDateTime dataCreazione,
                     LocalDateTime dataAggiornamento) {
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.numeroTelefono = numeroTelefono;
        this.email = email;
        this.partitaIva = partitaIva;
        this.dataCreazione = dataCreazione;
        this.dataAggiornamento = dataAggiornamento;
    }

    // Getter e Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getNumeroTelefono() { return numeroTelefono; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPartitaIva() { return partitaIva; }
    public void setPartitaIva(String partitaIva) { this.partitaIva = partitaIva; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataAggiornamento() { return dataAggiornamento; }
    public void setDataAggiornamento(LocalDateTime dataAggiornamento) { this.dataAggiornamento = dataAggiornamento; }
}
