package BusinessLogic.Strategy;

/**
 * Contesto unificato per l'esecuzione delle strategie di elaborazione dati.
 * Sostituisce sia CalculationContext che eventuali ReportContext separati.
 * Gestisce calcoli, statistiche e generazione di report in modo uniforme.
 */
public class DataProcessingContext {

    private DataProcessingStrategy<?> strategy;

    /**
     * Imposta la strategia di elaborazione da utilizzare
     * @param strategy La strategia da utilizzare
     */
    public void setStrategy(DataProcessingStrategy<?> strategy) {
        this.strategy = strategy;
    }

    /**
     * Esegue l'elaborazione utilizzando la strategia corrente
     * @return Il risultato dell'elaborazione
     */
    public Object executeProcessing() {
        if (strategy == null) {
            throw new IllegalStateException("Nessuna strategia di elaborazione impostata");
        }
        return strategy.execute();
    }

    /**
     * Restituisce il nome della strategia corrente
     * @return Nome della strategia
     */
    public String getCurrentStrategyName() {
        if (strategy == null) {
            return "Nessuna strategia impostata";
        }
        return strategy.getProcessingName();
    }

    /**
     * Restituisce il tipo di elaborazione della strategia corrente
     * @return Tipo di elaborazione
     */
    public DataProcessingStrategy.ProcessingType getCurrentProcessingType() {
        if (strategy == null) {
            return null;
        }
        return strategy.getProcessingType();
    }

    /**
     * Imposta i dati per la strategia corrente
     * @param data I dati da utilizzare
     */
    public void setData(Object... data) {
        if (strategy != null) {
            strategy.setData(data);
        }
    }

    /**
     * Metodo di convenienza per eseguire un'elaborazione completa
     * @param strategy La strategia da utilizzare
     * @param data I dati per l'elaborazione
     * @return Il risultato dell'elaborazione
     */
    public Object executeProcessing(DataProcessingStrategy<?> strategy, Object... data) {
        setStrategy(strategy);
        setData(data);
        return executeProcessing();
    }

    /**
     * Filtra ed esegue solo strategie di un tipo specifico
     * @param type Il tipo di elaborazione richiesto
     * @param strategy La strategia da verificare ed eseguire
     * @param data I dati per l'elaborazione
     * @return Il risultato se il tipo corrisponde, null altrimenti
     */
    public Object executeIfType(DataProcessingStrategy.ProcessingType type, DataProcessingStrategy<?> strategy, Object... data) {
        if (strategy.getProcessingType() == type) {
            return executeProcessing(strategy, data);
        }
        return null;
    }
}
