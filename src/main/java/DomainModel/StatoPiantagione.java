package DomainModel;

import java.time.LocalDateTime;


public class StatoPiantagione {
    private Integer id;
    private String codice;
    private String descrizione;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataAggiornamento;


    public StatoPiantagione() {}

    public StatoPiantagione(Integer id, String codice, String descrizione) {
        this.id = id;
        this.codice = codice;
        this.descrizione = descrizione;
    }

    public static final String ATTIVA = "ATTIVA";
    public static final String RIMOSSA = "RIMOSSA";
    public static final String IN_RACCOLTA = "IN_RACCOLTA";
    public static final String COMPLETATA = "COMPLETATA";
    public static final String SOSPESA = "SOSPESA";


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataAggiornamento() {
        return dataAggiornamento;
    }

    public void setDataAggiornamento(LocalDateTime dataAggiornamento) {
        this.dataAggiornamento = dataAggiornamento;
    }


    public boolean isAttiva() {
        return ATTIVA.equals(codice);
    }

    public boolean isRimossa() {
        return RIMOSSA.equals(codice);
    }

    public boolean isCompletata() {
        return COMPLETATA.equals(codice);
    }

    @Override
    public String toString() {
        return String.valueOf(descrizione);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StatoPiantagione that = (StatoPiantagione) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
