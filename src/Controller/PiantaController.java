package Controller;

import BussinesLogic.Service.PiantaService;
import DomainModel.Pianta;
import View.PiantaDialog;
import View.PiantaView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PiantaController {
    private final PiantaService piantaService;
    private final PiantaView piantaView;

    public PiantaController(PiantaService piantaService, PiantaView piantaView) {
        this.piantaService = piantaService;
        this.piantaView = piantaView;
        aggiornaView();
        piantaView.setOnNuovaPianta(v -> onNuovaPianta());
        piantaView.setOnModificaPianta(v -> onModificaPianta());
        piantaView.setOnEliminaPianta(v -> onEliminaPianta());
    }

    private void aggiornaView() {
        piantaView.setPiante(piantaService.getAllPiante());
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
