package BusinessLogic.Strategy;

import java.time.LocalDate;


public class StrategyFactory {
    public static DataProcessingStrategy<?> createStrategy(String strategiaName) {
        return switch (strategiaName) {
            // Strategie di calcolo (CALCULATION)
            case "Produzione Totale" -> new ProduzioneTotaleStrategy();
            case "Media per Pianta" -> new MediaProduzioneStrategy();
            case "Efficienza Produttiva" -> new EfficienzaProduttivaStrategy();
            case "Produzione per Periodo" -> new ProduzionePerPeriodoStrategy();

            // Strategie statistiche (STATISTICS)
            case "Piantagione Migliore", "Top Piantagioni" -> new TopPiantagioniStrategy();
            case "Statistiche Zone" -> new ReportStatisticheZonaStrategy();

            // Strategie di report (REPORT)
            case "Report Raccolti" -> new ReportRaccoltiStrategy();

            default -> throw new IllegalArgumentException("Strategia non riconosciuta: " + strategiaName);
        };
    }
}
