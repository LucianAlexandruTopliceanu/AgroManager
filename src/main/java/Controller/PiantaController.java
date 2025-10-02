package Controller;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.PiantaService;
import DomainModel.Pianta;
import View.PiantaDialog;
import View.PiantaView;
import View.NotificationHelper;

public class PiantaController {
    private final PiantaService piantaService;
    private final PiantaView piantaView;

    // Stato dei filtri
    private String filtroTipo = "";
    private String filtroVarieta = "";
    private String filtroFornitore = "";

    public PiantaController(PiantaService piantaService, PiantaView piantaView) {
        this.piantaService = piantaService;
        this.piantaView = piantaView;

        setupEventHandlers();
        aggiornaView();
    }

    private void setupEventHandlers() {
        piantaView.setOnNuovaPianta(this::onNuovaPianta);
        piantaView.setOnModificaPianta(this::onModificaPianta);
        piantaView.setOnEliminaPianta(this::onEliminaPianta);

        // Callback per i filtri
        piantaView.setOnTestoRicercaTipoChanged(this::onFiltroTipoChanged);
        piantaView.setOnTestoRicercaVarietaChanged(this::onFiltroVarietaChanged);
        piantaView.setOnFiltroFornitoreChanged(this::onFiltroFornitoreChanged);

        // Callback per aggiornamento dati
        piantaView.setOnAggiornaPiante(v -> aggiornaView());
    }

    private void onFiltroTipoChanged(String nuovoTipo) {
        filtroTipo = nuovoTipo != null ? nuovoTipo : "";
        aggiornaView();
    }

    private void onFiltroVarietaChanged(String nuovaVarieta) {
        filtroVarieta = nuovaVarieta != null ? nuovaVarieta : "";
        aggiornaView();
    }

    private void onFiltroFornitoreChanged(String nuovoFornitore) {
        filtroFornitore = nuovoFornitore != null ? nuovoFornitore : "";
        aggiornaView();
    }

    private void aggiornaView() {
        try {
            java.util.List<Pianta> tutte = piantaService.getAllPiante();

            // Applica i filtri
            java.util.List<Pianta> filtrate = tutte.stream()
                .filter(p -> filtroTipo.isEmpty() ||
                            p.getTipo().toLowerCase().contains(filtroTipo.toLowerCase()))
                .filter(p -> filtroVarieta.isEmpty() ||
                            p.getVarieta().toLowerCase().contains(filtroVarieta.toLowerCase()))
                .filter(p -> filtroFornitore.isEmpty() || "Tutti".equals(filtroFornitore) ||
                            String.valueOf(p.getFornitoreId()).equals(filtroFornitore))
                .toList();

            piantaView.setPiante(filtrate);

        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("caricamento piante", e);
        }
    }

    private void onNuovaPianta() {
        PiantaDialog dialog = new PiantaDialog(null); // Passa null per nuova pianta
        dialog.showAndWait();

        if (dialog.isConfermato()) {
            Pianta pianta = dialog.getPianta();
            try {
                piantaService.aggiungiPianta(pianta);
                NotificationHelper.showSuccess("Pianta aggiunta con successo!");
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("aggiunta pianta", e);
            }
        }
    }

    private void onModificaPianta() {
        Pianta selezionata = piantaView.getPiantaSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Seleziona una pianta da modificare");
            return;
        }

        PiantaDialog dialog = new PiantaDialog(selezionata);
        dialog.showAndWait();

        if (dialog.isConfermato()) {
            Pianta piantaModificata = dialog.getPianta();
            try {
                piantaService.aggiornaPianta(piantaModificata);
                NotificationHelper.showSuccess("Pianta aggiornata con successo!");
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("aggiornamento pianta", e);
            }
        }
    }

    private void onEliminaPianta() {
        Pianta selezionata = piantaView.getPiantaSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Seleziona una pianta da eliminare");
            return;
        }

        String messaggio = String.format("Sei sicuro di voler eliminare la pianta '%s - %s'?\n" +
                                        "Questa operazione non può essere annullata.",
                                        selezionata.getTipo(), selezionata.getVarieta());

        ErrorService.requestConfirmation("Conferma eliminazione", messaggio, confermato -> {
            if (confermato) {
                try {
                    piantaService.eliminaPianta(selezionata.getId());
                    NotificationHelper.showSuccess("Pianta eliminata con successo!");
                    aggiornaView();

                } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                    ErrorService.handleException(e);
                } catch (Exception e) {
                    ErrorService.handleException("eliminazione pianta", e);
                }
            }
        });
    }
}
