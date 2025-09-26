package Controller;

import BussinesLogic.RaccoltoService;
import BussinesLogic.PiantagioneService;
import DomainModel.Raccolto;
import DomainModel.Piantagione;
import View.RaccoltoDialog;
import View.RaccoltoView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.List;

public class RaccoltoController {
    private final RaccoltoService raccoltoService;
    private final PiantagioneService piantagioneService;
    private final RaccoltoView raccoltoView;

    public RaccoltoController(RaccoltoService raccoltoService, PiantagioneService piantagioneService, RaccoltoView raccoltoView) {
        this.raccoltoService = raccoltoService;
        this.piantagioneService = piantagioneService;
        this.raccoltoView = raccoltoView;
        aggiornaView();
        raccoltoView.setOnNuovoRaccolto(v -> onNuovoRaccolto());
        raccoltoView.setOnModificaRaccolto(v -> onModificaRaccolto());
        raccoltoView.setOnEliminaRaccolto(v -> onEliminaRaccolto());
    }

    private void aggiornaView() {
        raccoltoView.setRaccolti(raccoltoService.getAllRaccolti());
    }

    private void onNuovoRaccolto() {
        List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
        RaccoltoDialog dialog = new RaccoltoDialog(null, piantagioni);
        dialog.showAndWait();
        if (dialog.isConfermato()) {
            try {
                raccoltoService.aggiungiRaccolto(dialog.getRaccolto());
                aggiornaView();
            } catch (Exception ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void onModificaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato != null) {
            List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
            RaccoltoDialog dialog = new RaccoltoDialog(selezionato, piantagioni);
            dialog.showAndWait();
            if (dialog.isConfermato()) {
                try {
                    raccoltoService.aggiornaRaccolto(dialog.getRaccolto());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona un raccolto da modificare.");
        }
    }

    private void onEliminaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare il raccolto selezionato?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    raccoltoService.eliminaRaccolto(selezionato.getId());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona un raccolto da eliminare.");
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
