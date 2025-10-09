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

    // Costruttori
    public Piantagione() {
        // Default: stato ATTIVA (ID 1 nel database)
        this.idStatoPiantagione = 1;
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

    // NUOVI Getter e Setter per il ciclo di vita
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

    // Metodi di utilità per il ciclo di vita
    public boolean isAttiva() {
        return statoPiantagione != null ? statoPiantagione.isAttiva() :
               (idStatoPiantagione != null && idStatoPiantagione == 1);
    }

    public boolean isRimossa() {
        return statoPiantagione != null ? statoPiantagione.isRimossa() :
               (idStatoPiantagione != null && idStatoPiantagione == 2);
    }

    public boolean isCompletata() {
        return statoPiantagione != null ? statoPiantagione.isCompletata() :
               (idStatoPiantagione != null && idStatoPiantagione == 4);
    }

    /**
     * Metodo per cambiare lo stato della piantagione
     */
    public void cambiaStato(StatoPiantagione nuovoStato) {
        this.statoPiantagione = nuovoStato;
        this.idStatoPiantagione = nuovoStato.getId();
        this.dataAggiornamento = LocalDateTime.now();
    }

    /**
     * Metodo per rimuovere la piantagione
     */
    public void rimuovi() {
        this.idStatoPiantagione = 2; // ID dello stato RIMOSSA
        this.dataAggiornamento = LocalDateTime.now();
    }

    /**
     * Metodo per completare la piantagione
     */
    public void completa() {
        this.idStatoPiantagione = 4; // ID dello stato COMPLETATA
        // Sincronizza anche l'oggetto stato, se presente
        if (this.statoPiantagione != null) {
            this.statoPiantagione.setId(4);
            this.statoPiantagione.setCodice(StatoPiantagione.COMPLETATA);
        }
        this.dataAggiornamento = LocalDateTime.now();
    }

    /**
     * Metodo per riattivare una piantagione
     */
    public void riattiva() {
        this.idStatoPiantagione = 1; // ID dello stato ATTIVA
        this.dataAggiornamento = LocalDateTime.now();
    }

    /**
     * Calcola la durata in giorni dalla messa a dimora ad oggi
     */
    public long getDurataGiorni() {
        if (messaADimora == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(messaADimora, LocalDate.now());
    }

    /**
     * Restituisce una descrizione leggibile dello stato
     */
    public String getDescrizioneStato() {
        if (statoPiantagione != null) {
            return statoPiantagione.getDescrizione();
        }
        // Fallback basato sull'ID dello stato
        switch (idStatoPiantagione != null ? idStatoPiantagione : 1) {
            case 1: return "Attiva";
            case 2: return "Rimossa";
            case 3: return "In Raccolta";
            case 4: return "Completata";
            case 5: return "Sospesa";
            default: return "Sconosciuto";
        }
    }
}