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

public class ZonaController {
    private final ZonaService zonaService;
    private final ZonaView zonaView;

    // Stato dei filtri
    private String filtroNome = "";
    private String filtroTipo = "Tutti";

    public ZonaController(ZonaService zonaService, ZonaView zonaView) {
        this.zonaService = zonaService;
        this.zonaView = zonaView;

        setupEventHandlers();
        aggiornaView();
    }

    private void setupEventHandlers() {
        zonaView.setOnNuovaZona(this::onNuovaZona);
        zonaView.setOnModificaZona(this::onModificaZona);
        zonaView.setOnEliminaZona(this::onEliminaZona);

        // Callback per i filtri
        zonaView.setOnTestoRicercaChanged(this::onFiltroNomeChanged);
        zonaView.setOnTipoTerrenoChanged(this::onFiltroTipoChanged);
    }

    private void onFiltroNomeChanged(String nuovoNome) {
        filtroNome = nuovoNome != null ? nuovoNome : "";
        aggiornaView();
    }

    private void onFiltroTipoChanged(String nuovoTipo) {
        filtroTipo = nuovoTipo != null ? nuovoTipo : "Tutti";
        aggiornaView();
    }


    private void aggiornaView() {
        try {
            java.util.List<Zona> tutteLeZone = zonaService.getAllZone();

            // Applica i filtri
            java.util.List<Zona> filtrate = tutteLeZone.stream()
                .filter(z -> filtroNome.isEmpty() ||
                            (z.getNome() != null && z.getNome().toLowerCase().contains(filtroNome.toLowerCase())))
                .filter(z -> filtroTipo.equals("Tutti") ||
                            (z.getTipoTerreno() != null && z.getTipoTerreno().equalsIgnoreCase(filtroTipo)))
                .toList();

            zonaView.setZone(filtrate);

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
            Zona zona = dialog.getZona();
            try {
                zonaService.aggiungiZona(zona);
                NotificationHelper.showSuccess("Operazione completata", "Zona aggiunta con successo!");
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
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
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
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

        if (NotificationHelper.confirmCriticalOperation("Eliminazione Zona",
                "Zona: " + selezionata.getNome())) {
            try {
                zonaService.eliminaZona(selezionata.getId());
                NotificationHelper.showSuccess("Operazione completata", "Zona eliminata con successo!");
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("eliminazione zona", e);
            }
        }
    }
}
