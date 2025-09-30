package Controller;

import BusinessLogic.Service.PiantaService;
import DomainModel.Pianta;
import View.PiantaDialog;
import View.PiantaView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PiantaController {
    private final PiantaService piantaService;
    private final PiantaView piantaView;

    // Stato dei filtri
    private String filtroTipo = "";
    private String filtroVarieta = "";
    private String filtroFornitore = "Tutti";

    public PiantaController(PiantaService piantaService, PiantaView piantaView) {
        this.piantaService = piantaService;
        this.piantaView = piantaView;
        aggiornaView();
        piantaView.setOnNuovaPianta(this::onNuovaPianta);
        piantaView.setOnModificaPianta(this::onModificaPianta);
        piantaView.setOnEliminaPianta(this::onEliminaPianta);
        // Callback per i filtri
        piantaView.setOnTestoRicercaTipoChanged(this::onFiltroTipoChanged);
        piantaView.setOnTestoRicercaVarietaChanged(this::onFiltroVarietaChanged);
        piantaView.setOnFiltroFornitoreChanged(this::onFiltroFornitoreChanged);
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
        filtroFornitore = nuovoFornitore != null ? nuovoFornitore : "Tutti";
        aggiornaView();
    }

    private void aggiornaView() {
        java.util.List<Pianta> tutte = piantaService.getAllPiante();
        java.util.List<Pianta> filtrate = tutte.stream()
            .filter(p -> filtroTipo.isEmpty() || (p.getTipo() != null && p.getTipo().toLowerCase().contains(filtroTipo.toLowerCase())))
            .filter(p -> filtroVarieta.isEmpty() || (p.getVarieta() != null && p.getVarieta().toLowerCase().contains(filtroVarieta.toLowerCase())))
            .filter(p -> filtroFornitore.equals("Tutti") || (p.getFornitoreId() != null && p.getFornitoreId().toString().equals(filtroFornitore)))
            .toList();
        piantaView.setPiante(filtrate);
    }

    private void onNuovaPianta() {
        PiantaDialog dialog = new PiantaDialog(null);
        dialog.showAndWait();
        if (dialog.isConfermato()) {
            try {
                piantaService.aggiungiPianta(dialog.getPianta());
                aggiornaView();
            } catch (Exception ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void onModificaPianta() {
        Pianta selezionata = piantaView.getPiantaSelezionata();
        if (selezionata != null) {
            PiantaDialog dialog = new PiantaDialog(selezionata);
            dialog.showAndWait();
            if (dialog.isConfermato()) {
                try {
                    piantaService.aggiornaPianta(dialog.getPianta());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una pianta da modificare.");
        }
    }

    private void onEliminaPianta() {
        Pianta selezionata = piantaView.getPiantaSelezionata();
        if (selezionata != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare la pianta selezionata?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    piantaService.eliminaPianta(selezionata.getId());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una pianta da eliminare.");
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
