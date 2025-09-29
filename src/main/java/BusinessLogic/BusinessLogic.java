package BusinessLogic;

import BusinessLogic.Strategy.*;
import DomainModel.Piantagione;
import DomainModel.Raccolto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Classe principale per la logica di business dell'applicazione AgroManager.
 * Utilizza il pattern Strategy unificato per delegare elaborazioni alle strategie specifiche.
 */
public class BusinessLogic {

    private final DataProcessingContext processingContext;

    public BusinessLogic() {
        this.processingContext = new DataProcessingContext();
    }

    /**
     * Calcola la produzione totale di una specifica piantagione
     */
    public BigDecimal calcolaProduzioneTotalePiantagione(List<Raccolto> raccolti, int piantagioneId) {
        return (BigDecimal) processingContext.executeProcessing(
            new ProduzioneTotaleStrategy(), raccolti, piantagioneId);
    }

    /**
     * Calcola la media di produzione per pianta di una specifica piantagione
     */
    public BigDecimal calcolaMediaProduzionePianta(List<Raccolto> raccolti, List<Piantagione> piantagioni, int piantagioneId) {
        return (BigDecimal) processingContext.executeProcessing(
            new MediaProduzioneStrategy(), raccolti, piantagioni, piantagioneId);
    }

    /**
     * Trova l'ID della piantagione più produttiva
     */
    public int trovaIdPiantagionePiuProduttiva(List<Raccolto> raccolti) {
        return (Integer) processingContext.executeProcessing(
            new PiantagionePiuProduttivaStrategy(), raccolti);
    }

    /**
     * Calcola l'efficienza produttiva (kg per pianta per giorno)
     */
    public BigDecimal calcolaEfficienzaProduttiva(List<Raccolto> raccolti, List<Piantagione> piantagioni, int piantagioneId) {
        return (BigDecimal) processingContext.executeProcessing(
            new EfficienzaProduttivaStrategy(), raccolti, piantagioni, piantagioneId);
    }

    /**
     * Calcola la produzione totale per periodo
     */
    public BigDecimal calcolaProduzionePerPeriodo(List<Raccolto> raccolti, LocalDate inizio, LocalDate fine) {
        return (BigDecimal) processingContext.executeProcessing(
            new ProduzionePerPeriodoStrategy(), raccolti, inizio, fine);
    }

    /**
     * Trova le piantagioni più produttive (top N)
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, BigDecimal> trovaTopPiantagioniProduttive(List<Raccolto> raccolti, int topN) {
        return (Map<Integer, BigDecimal>) processingContext.executeProcessing(
            new TopPiantagioniStrategy(), raccolti, topN);
    }

    /**
     * Genera un report testuale dei raccolti
     */
    public String generaReportRaccolti(List<Raccolto> raccolti) {
        return (String) processingContext.executeProcessing(
            new ReportRaccoltiStrategy(), raccolti);
    }

    /**
     * Metodo di convenienza per eseguire elaborazioni personalizzate
     */
    public Object eseguiElaborazione(DataProcessingStrategy<?> strategy, Object... data) {
        return processingContext.executeProcessing(strategy, data);
    }

    /**
     * Esegue elaborazioni filtrate per tipo
     */
    public Object eseguiElaborazionePerTipo(DataProcessingStrategy.ProcessingType type,
                                          DataProcessingStrategy<?> strategy, Object... data) {
        return processingContext.executeIfType(type, strategy, data);
    }

    // Metodi di utilità che non necessitano di Strategy pattern

    /**
     * Verifica se un raccolto è avvenuto in un periodo specifico
     */
    public boolean isRaccoltoInPeriodo(LocalDate dataRaccolto, LocalDate inizio, LocalDate fine) {
        if (dataRaccolto == null || inizio == null || fine == null) {
            return false;
        }
        return !dataRaccolto.isBefore(inizio) && !dataRaccolto.isAfter(fine);
    }

    /**
     * Calcola la resa media per metro quadrato (se disponibile la dimensione zona)
     */
    public BigDecimal calcolaResaPerMetroQuadrato(List<Raccolto> raccolti, List<Piantagione> piantagioni,
                                                 int piantagioneId, double dimensioneZona) {
        BigDecimal totaleProduzione = calcolaProduzioneTotalePiantagione(raccolti, piantagioneId);

        if (dimensioneZona <= 0) {
            return BigDecimal.ZERO;
        }

        // Converti ettari in metri quadrati (1 ettaro = 10000 m²)
        BigDecimal metriQuadrati = new BigDecimal(dimensioneZona * 10000);

        return totaleProduzione.divide(metriQuadrati, 4, java.math.RoundingMode.HALF_UP);
    }
}
