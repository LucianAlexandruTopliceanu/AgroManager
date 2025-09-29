package BusinessLogic.Strategy;

/**
 * Interfaccia unificata per tutte le strategie di elaborazione dati dell'applicazione AgroManager.
 * Sostituisce sia CalculationStrategy che ReportStrategy con un approccio più coerente.
 * Supporta calcoli, statistiche e generazione di report.
 */
public interface DataProcessingStrategy<T> {

    /**
     * Esegue l'elaborazione specifica della strategia
     * @return Il risultato dell'elaborazione
     */
    T execute();

    /**
     * Restituisce il nome descrittivo dell'elaborazione
     * @return Nome dell'elaborazione
     */
    String getProcessingName();

    /**
     * Restituisce la categoria del tipo di elaborazione
     * @return Categoria (CALCULATION, REPORT, STATISTICS)
     */
    ProcessingType getProcessingType();

    /**
     * Imposta i dati necessari per l'elaborazione
     * @param data I dati da utilizzare
     */
    void setData(Object... data);

    /**
     * Enum per categorizzare i tipi di elaborazione
     */
    enum ProcessingType {
        CALCULATION,    // Calcoli numerici (produzione, efficienza, etc.)
        REPORT,         // Generazione di report testuali
        STATISTICS      // Analisi statistiche (top, medie, etc.)
    }
}
