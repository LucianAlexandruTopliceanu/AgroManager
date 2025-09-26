package Controller;

import BussinesLogic.PiantagioneService;
import BussinesLogic.ZonaService;
import BussinesLogic.PiantaService;
import DomainModel.Piantagione;
import View.PiantagioneDialog;
import View.PiantagioneView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PiantagioneController {
    private final PiantagioneService piantagioneService;
    private final ZonaService zonaService;
    private final PiantaService piantaService;
    private final PiantagioneView piantagioneView;

    // Il costruttore ora richiede anche ZonaService e PiantaService
    public PiantagioneController(PiantagioneService piantagioneService, ZonaService zonaService, PiantaService piantaService, PiantagioneView piantagioneView) {
        this.piantagioneService = piantagioneService;
        this.zonaService = zonaService;
        this.piantaService = piantaService;
        this.piantagioneView = piantagioneView;
        aggiornaView();
        piantagioneView.setOnNuovaPiantagione(v -> onNuovaPiantagione());
        piantagioneView.setOnModificaPiantagione(v -> onModificaPiantagione());
        piantagioneView.setOnEliminaPiantagione(v -> onEliminaPiantagione());
    }

    private void aggiornaView() {
        piantagioneView.setPiantagioni(piantagioneService.getAllPiantagioni());
    }

    private void onNuovaPiantagione() {
        PiantagioneDialog dialog = new PiantagioneDialog(null, zonaService.getAllZone(), piantaService.getAllPiante());
        dialog.showAndWait();
        if (dialog.isConfermato()) {
            try {
                piantagioneService.aggiungiPiantagione(dialog.getPiantagione());
                aggiornaView();
            } catch (Exception ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void onModificaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata != null) {
            PiantagioneDialog dialog = new PiantagioneDialog(selezionata, zonaService.getAllZone(), piantaService.getAllPiante());
            dialog.showAndWait();
            if (dialog.isConfermato()) {
                try {
                    piantagioneService.aggiornaPiantagione(dialog.getPiantagione());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una piantagione da modificare.");
        }
    }

    private void onEliminaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare la piantagione selezionata?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    piantagioneService.eliminaPiantagione(selezionata.getId());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una piantagione da eliminare.");
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
