package BusinessLogic.Strategy;


public class StrategyFactory {
    public static DataProcessingStrategy<?> createStrategy(String strategiaName) {
        return switch (strategiaName) {
            case "Produzione Totale" -> new ProduzioneTotaleStrategy();
            case "Media per Pianta" -> new MediaProduzioneStrategy();
            case "Efficienza Produttiva" -> new EfficienzaProduttivaStrategy();
            case "Produzione per Periodo" -> new ProduzionePerPeriodoStrategy();
            case "Periodo Coperto Raccolti" -> new PeriodoCopertoRaccoltiStrategy();
            case "Piantagione Migliore", "Top Piantagioni" -> new TopPiantagioniStrategy();
            case "Statistiche Zone" -> new ReportStatisticheZonaStrategy();
            case "Statistiche Generali Raccolti" -> new StatisticheGeneraliRaccoltiStrategy();
            case "Statistiche Mensili Raccolti" -> new StatisticheMensiliRaccoltiStrategy();
            case "Report Raccolti" -> new ReportRaccoltiStrategy();
            default -> throw new IllegalArgumentException("Strategia non riconosciuta: " + strategiaName);
        };
    }
}
