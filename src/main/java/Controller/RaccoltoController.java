package Controller;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.RaccoltoService;
import BusinessLogic.Service.PiantagioneService;
import DomainModel.Raccolto;
import DomainModel.Piantagione;
import View.RaccoltoDialog;
import View.RaccoltoView;
import View.NotificationHelper;
import java.util.List;

public class RaccoltoController {
    private final RaccoltoService raccoltoService;
    private final PiantagioneService piantagioneService;
    private final RaccoltoView raccoltoView;

    // Stato dei filtri
    private String filtroPiantagione = "Tutte";
    private java.time.LocalDate filtroDataDa = null;
    private java.time.LocalDate filtroDataA = null;
    private double filtroQuantitaMin = 0.0;
    private double filtroQuantitaMax = 1000.0;

    public RaccoltoController(RaccoltoService raccoltoService, PiantagioneService piantagioneService, RaccoltoView raccoltoView) {
        this.raccoltoService = raccoltoService;
        this.piantagioneService = piantagioneService;
        this.raccoltoView = raccoltoView;

        setupEventHandlers();
        aggiornaView();
    }

    private void setupEventHandlers() {
        raccoltoView.setOnNuovoRaccolto(this::onNuovoRaccolto);
        raccoltoView.setOnModificaRaccolto(this::onModificaRaccolto);
        raccoltoView.setOnEliminaRaccolto(this::onEliminaRaccolto);

        // Callback per i filtri
        raccoltoView.setOnFiltroPiantagioneChanged(this::onFiltroPiantagioneChanged);
        raccoltoView.setOnFiltroDataDaChanged(this::onFiltroDataDaChanged);
        raccoltoView.setOnFiltroDataAChanged(this::onFiltroDataAChanged);
        raccoltoView.setOnFiltroQuantitaMinChanged(this::onFiltroQuantitaMinChanged);
        raccoltoView.setOnFiltroQuantitaMaxChanged(this::onFiltroQuantitaMaxChanged);
    }

    private void onFiltroPiantagioneChanged(String nuovaPiantagione) {
        filtroPiantagione = nuovaPiantagione != null ? nuovaPiantagione : "Tutte";
        aggiornaView();
    }

    private void onFiltroDataDaChanged(java.time.LocalDate nuovaDataDa) {
        filtroDataDa = nuovaDataDa;
        aggiornaView();
    }

    private void onFiltroDataAChanged(java.time.LocalDate nuovaDataA) {
        filtroDataA = nuovaDataA;
        aggiornaView();
    }

    private void onFiltroQuantitaMinChanged(Double nuovaMin) {
        filtroQuantitaMin = nuovaMin != null ? nuovaMin : 0.0;
        aggiornaView();
    }

    private void onFiltroQuantitaMaxChanged(Double nuovaMax) {
        filtroQuantitaMax = nuovaMax != null ? nuovaMax : 1000.0;
        aggiornaView();
    }


    private void aggiornaView() {
        try {
            java.util.List<Raccolto> tutti = raccoltoService.getAllRaccolti();

            // Applica i filtri
            java.util.List<Raccolto> filtrati = tutti.stream()
                .filter(r -> filtroPiantagione.equals("Tutte") ||
                            (r.getPiantagioneId() != null && r.getPiantagioneId().toString().equals(filtroPiantagione)))
                .filter(r -> filtroDataDa == null ||
                            (r.getDataRaccolto() != null && !r.getDataRaccolto().isBefore(filtroDataDa)))
                .filter(r -> filtroDataA == null ||
                            (r.getDataRaccolto() != null && !r.getDataRaccolto().isAfter(filtroDataA)))
                .filter(r -> {
                    double q = r.getQuantitaKg() != null ? r.getQuantitaKg().doubleValue() : 0.0;
                    return q >= filtroQuantitaMin && q <= filtroQuantitaMax;
                })
                .toList();

            raccoltoView.setRaccolti(filtrati);

        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("caricamento raccolti", e);
        }
    }

    public void onNuovoRaccolto() {
        try {
            java.util.List<Raccolto> tutti = raccoltoService.getAllRaccolti();
            List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
            RaccoltoDialog dialog = new RaccoltoDialog(null, piantagioni);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                Raccolto raccolto = dialog.getRaccolto();
                try {
                    raccoltoService.aggiungiRaccolto(raccolto);
                    NotificationHelper.showSuccess("Operazione completata", "Raccolto aggiunto con successo!");
                    aggiornaView();

                } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                    ErrorService.handleException(e);
                } catch (Exception e) {
                    ErrorService.handleException("aggiunta raccolto", e);
                }
            }
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        }
    }

    public void onModificaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona un raccolto da modificare");
            return;
        }

        try {
            List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
            RaccoltoDialog dialog = new RaccoltoDialog(selezionato, piantagioni);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                try {
                    raccoltoService.aggiornaRaccolto(dialog.getRaccolto());
                    NotificationHelper.showSuccess("Operazione completata", "Raccolto aggiornato con successo!");
                    aggiornaView();

                } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                    ErrorService.handleException(e);
                } catch (Exception e) {
                    ErrorService.handleException("aggiornamento raccolto", e);
                }
            }
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        }
    }

    public void onEliminaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona un raccolto da eliminare");
            return;
        }

        if (NotificationHelper.confirmCriticalOperation("Eliminazione Raccolto",
                "Raccolto del " + selezionato.getDataRaccolto())) {
            try {
                raccoltoService.eliminaRaccolto(selezionato.getId());
                NotificationHelper.showSuccess("Operazione completata", "Raccolto eliminato con successo!");
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("eliminazione raccolto", e);
            }
        }
    }
}
