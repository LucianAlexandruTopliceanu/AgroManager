package Controller;

import BussinesLogic.FornitoreService;
import DomainModel.Fornitore;
import View.FornitoreDialog;
import View.FornitoreView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class FornitoreController {
    private final FornitoreService fornitoreService;
    private final FornitoreView fornitoreView;

    public FornitoreController(FornitoreService fornitoreService, FornitoreView fornitoreView) {
        this.fornitoreService = fornitoreService;
        this.fornitoreView = fornitoreView;
        aggiornaView();
        fornitoreView.setOnNuovoFornitore(v -> onNuovoFornitore());
        fornitoreView.setOnModificaFornitore(v -> onModificaFornitore());
        fornitoreView.setOnEliminaFornitore(v -> onEliminaFornitore());
    }

    private void aggiornaView() {
        fornitoreView.setFornitori(fornitoreService.getAllFornitori());
    }

    private void onNuovoFornitore() {
        FornitoreDialog dialog = new FornitoreDialog(null);
        dialog.showAndWait();
        if (dialog.isConfermato()) {
            try {
                fornitoreService.aggiungiFornitore(dialog.getFornitore());
                aggiornaView();
            } catch (Exception ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void onModificaFornitore() {
        Fornitore selezionato = fornitoreView.getFornitoreSelezionato();
        if (selezionato != null) {
            FornitoreDialog dialog = new FornitoreDialog(selezionato);
            dialog.showAndWait();
            if (dialog.isConfermato()) {
                try {
                    fornitoreService.aggiornaFornitore(dialog.getFornitore());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona un fornitore da modificare.");
        }
    }

    private void onEliminaFornitore() {
        Fornitore selezionato = fornitoreView.getFornitoreSelezionato();
        if (selezionato != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare il fornitore selezionato?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    fornitoreService.eliminaFornitore(selezionato.getId());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona un fornitore da eliminare.");
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
