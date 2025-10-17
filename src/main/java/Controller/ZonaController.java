package Controller;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.ZonaService;
import DomainModel.Zona;
import View.ZonaDialog;
import View.ZonaView;
import View.NotificationHelper;

import java.util.List;

public class ZonaController {
    private final ZonaService zonaService;
    private final ZonaView zonaView;

    public ZonaController(ZonaService zonaService, ZonaView zonaView) {
        this.zonaService = zonaService;
        this.zonaView = zonaView;

        setupEventHandlers();
        inizializzaFiltri();
        aggiornaListaZone();
    }

    private void setupEventHandlers() {
        zonaView.setOnNuovaZona(this::onNuovaZona);
        zonaView.setOnModificaZona(this::onModificaZona);
        zonaView.setOnEliminaZona(this::onEliminaZona);
        zonaView.setOnApplicaFiltri(this::onApplicaFiltri);
        zonaView.setOnResetFiltri(this::onResetFiltri);
    }

    private void inizializzaFiltri() {
        try {
            List<String> tipiTerreno = zonaService.getTipiTerrenoDisponibili();
            zonaView.setTipiTerreno(tipiTerreno);
        } catch (Exception e) {
            ErrorService.handleException("inizializzazione filtri", e);
        }
    }

    private void onApplicaFiltri() {
        try {
            var criteriFiltro = zonaView.getCriteriFiltro();
            var zoneFiltrate = zonaService.getZoneConFiltri(criteriFiltro);
            zonaView.setZone(zoneFiltrate);
        } catch (Exception e) {
            ErrorService.handleException("applicazione filtri", e);
        }
    }

    private void onResetFiltri() {
        zonaView.resetFiltri();
        aggiornaListaZone();
    }

    private void aggiornaListaZone() {
        try {
            var tutte = zonaService.getAllZone();
            zonaView.setZone(tutte);
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("caricamento zone", e);
        }
    }

    public void onNuovaZona() {
        ZonaDialog dialog = new ZonaDialog(new Zona());
        dialog.showAndWait();

        if (dialog.isConfermato()) {
            try {
                Zona zona = dialog.getZona();
                zonaService.aggiungiZona(zona);
                NotificationHelper.showSuccess("Operazione completata", "Zona aggiunta con successo!");
                aggiornaListaZone();
            } catch (ValidationException | DataAccessException | BusinessLogicException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("aggiunta zona", e);
            }
        }
    }

    public void onModificaZona() {
        Zona selezionata = zonaView.getZonaSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una zona da modificare");
            return;
        }

        ZonaDialog dialog = new ZonaDialog(selezionata);
        dialog.showAndWait();

        if (dialog.isConfermato()) {
            try {
                zonaService.aggiornaZona(dialog.getZona());
                NotificationHelper.showSuccess("Operazione completata", "Zona aggiornata con successo!");
                aggiornaListaZone();
            } catch (ValidationException | DataAccessException | BusinessLogicException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("aggiornamento zona", e);
            }
        }
    }

    public void onEliminaZona() {
        Zona selezionata = zonaView.getZonaSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una zona da eliminare");
            return;
        }

        try {
            boolean confermato = zonaView.confermaEliminazione(selezionata);
            if (confermato) {
                zonaService.eliminaZona(selezionata.getId());
                NotificationHelper.showSuccess("Operazione completata", "Zona eliminata con successo!");
                aggiornaListaZone();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("eliminazione zona", e);
        }
    }

    public void refreshData() {
        aggiornaListaZone();
    }
}
