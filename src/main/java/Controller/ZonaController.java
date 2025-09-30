package Controller;

import BusinessLogic.Service.ZonaService;
import DomainModel.Zona;
import View.ZonaDialog;
import View.ZonaView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ZonaController {
    private final ZonaService zonaService;
    private final ZonaView zonaView;

    // Stato dei filtri
    private String filtroNome = "";
    private String filtroTipo = "Tutti";

    public ZonaController(ZonaService zonaService, ZonaView zonaView) {
        this.zonaService = zonaService;
        this.zonaView = zonaView;
        aggiornaView();
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
        java.util.List<Zona> tutteLeZone = zonaService.getAllZone();
        java.util.List<Zona> filtrate = tutteLeZone.stream()
            .filter(z -> filtroNome.isEmpty() || (z.getNome() != null && z.getNome().toLowerCase().contains(filtroNome.toLowerCase())))
            .filter(z -> filtroTipo.equals("Tutti") || (z.getTipoTerreno() != null && z.getTipoTerreno().equalsIgnoreCase(filtroTipo)))
            .toList();
        zonaView.setZone(filtrate);
    }

    private void onNuovaZona() {
        ZonaDialog dialog = new ZonaDialog(null);
        dialog.showAndWait();
        if (dialog.isConfermato()) {
            try {
                zonaService.aggiungiZona(dialog.getZona());
                aggiornaView();
            } catch (Exception ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void onModificaZona() {
        Zona selezionata = zonaView.getZonaSelezionata();
        if (selezionata != null) {
            ZonaDialog dialog = new ZonaDialog(selezionata);
            dialog.showAndWait();
            if (dialog.isConfermato()) {
                try {
                    zonaService.aggiornaZona(dialog.getZona());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una zona da modificare.");
        }
    }

    private void onEliminaZona() {
        Zona selezionata = zonaView.getZonaSelezionata();
        if (selezionata != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare la zona selezionata?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    zonaService.eliminaZona(selezionata.getId());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una zona da eliminare.");
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
