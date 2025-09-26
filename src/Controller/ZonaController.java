package Controller;

import BussinesLogic.ZonaService;
import DomainModel.Zona;
import View.ZonaDialog;
import View.ZonaView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.List;

public class ZonaController {
    private final ZonaService zonaService;
    private final ZonaView zonaView;

    public ZonaController(ZonaService zonaService, ZonaView zonaView) {
        this.zonaService = zonaService;
        this.zonaView = zonaView;
        aggiornaView();
        zonaView.setOnNuovaZona(v -> onNuovaZona());
        zonaView.setOnModificaZona(v -> onModificaZona());
        zonaView.setOnEliminaZona(v -> onEliminaZona());
    }

    private void aggiornaView() {
        zonaView.setZone(zonaService.getAllZone());
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
