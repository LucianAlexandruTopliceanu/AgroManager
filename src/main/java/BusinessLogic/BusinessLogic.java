package BusinessLogic;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.RaccoltoService;
import BusinessLogic.Service.ReportService;
import BusinessLogic.Strategy.*;
import DomainModel.*;
import ORM.DAOFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

public class BusinessLogic {
    private final RaccoltoService raccoltoService;
    private final ReportService reportService;

    public BusinessLogic() {
        this.raccoltoService = new RaccoltoService(DAOFactory.getInstance().getRaccoltoDAO());
        this.reportService = new ReportService(raccoltoService);
    }

    public ReportService getReportService() {
        return reportService;
    }

    public ProcessingResult<?> eseguiStrategiaConDati(DataProcessingStrategy.ProcessingType tipo, String strategia, String piantagioneId,
                                                       LocalDate dataInizio, LocalDate dataFine, Integer topN)
            throws ValidationException, DataAccessException, BusinessLogicException {
        try {
            DAOFactory daoFactory = DAOFactory.getInstance();
            List<Raccolto> raccolti = daoFactory.getRaccoltoDAO().findAll();
            List<Piantagione> piantagioni = daoFactory.getPiantagioneDAO().findAll();
            List<Zona> zone = daoFactory.getZonaDAO().findAll();

            DataProcessingStrategy<?> strategy = StrategyFactory.createStrategy(strategia);

            if (strategy.getType() != tipo) {
                throw new ValidationException("tipoStrategia", strategia,
                        "non è del tipo richiesto " + tipo);
            }

            Object[] params = prepareParameters(strategy, raccolti, piantagioni, zone,
                piantagioneId, dataInizio, dataFine, topN);

            return strategy.execute(params);

        } catch (SQLException e) {
            throw DataAccessException.queryError("recupero dati per elaborazione", e);
        }
    }

    public String eseguiStrategia(DataProcessingStrategy.ProcessingType tipo, String strategia, String piantagioneId,
                                  LocalDate dataInizio, LocalDate dataFine, Integer topN) {
        try {
            ProcessingResult<?> result = eseguiStrategiaConDati(tipo, strategia, piantagioneId, dataInizio, dataFine, topN);
            return formatResult(result, strategia, piantagioneId, dataInizio, dataFine, topN);

        } catch (ValidationException e) {
            ErrorService.handleException(e);
            return "Errore di validazione: " + e.getUserMessage();
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
            return "Errore di accesso ai dati: " + e.getUserMessage();
        } catch (BusinessLogicException e) {
            ErrorService.handleException(e);
            return "Errore di business logic: " + e.getUserMessage();
        } catch (Exception e) {
            ErrorService.handleException("esecuzione strategia", e);
            return "Errore imprevisto durante l'elaborazione";
        }
    }


    private String formatResult(ProcessingResult<?> result, String strategia, String piantagioneId,
                               LocalDate dataInizio, LocalDate dataFine, Integer topN) {
        Object value = result.getValue();

        return switch (strategia) {
            case "Produzione Totale" -> String.format("Produzione totale per piantagione %s: %.2f kg",
                piantagioneId, (BigDecimal) value);

            case "Media per Pianta" -> String.format("Media produzione per piantagione %s: %.2f kg/pianta",
                piantagioneId, (BigDecimal) value);

            case "Efficienza Produttiva" -> String.format("Efficienza piantagione %s: %.4f kg/pianta/giorno",
                piantagioneId, (BigDecimal) value);

            case "Produzione per Periodo" -> String.format("Produzione dal %s al %s: %.2f kg",
                dataInizio, dataFine, (BigDecimal) value);

            case "Top Piantagioni" -> formatTopPiantagioni((Map<Integer, BigDecimal>) value, topN);

            default -> "Risultato: " + value.toString();
        };
    }

    private String formatTopPiantagioni(Map<Integer, BigDecimal> topPiantagioni, Integer topN) {
        if (topPiantagioni.isEmpty()) {
            return "⚠Nessuna piantagione trovata nei dati";
        }

        if (topN == null || topN == 1) {
            Map.Entry<Integer, BigDecimal> best = topPiantagioni.entrySet().iterator().next();
            return String.format("Piantagione più produttiva:\nID: %d\nProduzione totale: %.2f kg",
                best.getKey(), best.getValue());
        } else {
            StringBuilder sb = getStringBuilder(topPiantagioni, topN);
            return sb.toString();
        }
    }

    private static StringBuilder getStringBuilder(Map<Integer, BigDecimal> topPiantagioni, Integer topN) {
        StringBuilder sb = new StringBuilder(String.format("Top %d piantagioni per produzione:\n",
            Math.min(topN, topPiantagioni.size())));
        int pos = 1;
        for (Map.Entry<Integer, BigDecimal> entry : topPiantagioni.entrySet()) {
            String medal = "";
            sb.append(String.format("%s #%d: Piantagione %d - %.2f kg\n",
                medal, pos++, entry.getKey(), entry.getValue()));
        }
        return sb;
    }

    private Object[] prepareParameters(DataProcessingStrategy<?> strategy,
                                     List<Raccolto> raccolti,
                                     List<Piantagione> piantagioni,
                                     List<Zona> zone,
                                     String piantagioneId,
                                     LocalDate dataInizio,
                                     LocalDate dataFine,
                                     Integer topN) throws ValidationException {

        if (strategy instanceof ProduzioneTotaleStrategy) {
            return preparePiantagioneParams(raccolti, piantagioneId);

        } else if (strategy instanceof MediaProduzioneStrategy ||
                   strategy instanceof EfficienzaProduttivaStrategy) {
            return preparePiantagioneWithListParams(raccolti, piantagioni, piantagioneId);

        } else if (strategy instanceof ProduzionePerPeriodoStrategy) {
            return preparePeriodoParams(raccolti, dataInizio, dataFine);

        } else if (strategy instanceof TopPiantagioniStrategy) {
            return prepareTopParams(raccolti, topN);

        } else if (strategy instanceof ReportStatisticheZonaStrategy) {
            return new Object[]{raccolti, piantagioni, zone};

        } else if (strategy instanceof ReportRaccoltiStrategy) {
            return new Object[]{raccolti, piantagioni};

        } else {
            return new Object[]{raccolti};
        }
    }

    private Object[] preparePiantagioneParams(List<Raccolto> raccolti, String piantagioneId) throws ValidationException {
        if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
            throw ValidationException.requiredField("ID piantagione");
        }

        try {
            return new Object[]{raccolti, Integer.parseInt(piantagioneId)};
        } catch (NumberFormatException e) {
            throw ValidationException.invalidFormat("ID piantagione", "numero intero");
        }
    }

    private Object[] preparePiantagioneWithListParams(List<Raccolto> raccolti, List<Piantagione> piantagioni,
                                                    String piantagioneId) throws ValidationException {
        if (piantagioneId == null || piantagioneId.trim().isEmpty()) {
            throw ValidationException.requiredField("ID piantagione");
        }

        try {
            return new Object[]{raccolti, piantagioni, Integer.parseInt(piantagioneId)};
        } catch (NumberFormatException e) {
            throw ValidationException.invalidFormat("ID piantagione", "numero intero");
        }
    }

    private Object[] preparePeriodoParams(List<Raccolto> raccolti, LocalDate dataInizio, LocalDate dataFine)
            throws ValidationException {
        isDataRangeValid(dataInizio, dataFine);

        return new Object[]{raccolti, dataInizio, dataFine};
    }

    public static void isDataRangeValid(LocalDate dataInizio, LocalDate dataFine) throws ValidationException {
        if (dataInizio == null) {
            throw ValidationException.requiredField("Data inizio periodo");
        }
        if (dataFine == null) {
            throw ValidationException.requiredField("Data fine periodo");
        }
        if (dataFine.isBefore(dataInizio)) {
            throw new ValidationException("periodo", dataInizio + " - " + dataFine,
                    "la data di fine non può essere precedente alla data di inizio");
        }
    }

    private Object[] prepareTopParams(List<Raccolto> raccolti, Integer topN) throws ValidationException {
        int n = topN != null ? topN : 1;

        if (n <= 0) {
            throw new ValidationException("topN", String.valueOf(n),
                    "deve essere maggiore di zero");
        }

        return new Object[]{raccolti, n};
    }

    public boolean isRaccoltoInPeriodo(LocalDate dataRaccolto, LocalDate inizio, LocalDate fine) {
        if (dataRaccolto == null || inizio == null || fine == null) {
            return false;
        }
        return !dataRaccolto.isBefore(inizio) && !dataRaccolto.isAfter(fine);
    }

    public Map<String, List<String>> getDatiPerComboBox() throws DataAccessException {
        try {
            DAOFactory daoFactory = DAOFactory.getInstance();
            var piantagioni = daoFactory.getPiantagioneDAO().findAll();
            var zone = daoFactory.getZonaDAO().findAll();

            return Map.of(
                "piantagioni", piantagioni.stream().map(p -> p.getId().toString()).toList(),
                "zone", zone.stream().map(z -> z.getNome()).toList()
            );
        } catch (SQLException e) {
            throw DataAccessException.queryError("recupero dati combo box", e);
        }
    }
}
