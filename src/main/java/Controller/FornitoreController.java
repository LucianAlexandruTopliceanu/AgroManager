package Controller;

import BusinessLogic.Service.FornitoreService;
import DomainModel.Fornitore;
import View.FornitoreDialog;
import View.FornitoreView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class FornitoreController {
    private final FornitoreService fornitoreService;
    private final FornitoreView fornitoreView;

    // Stato dei filtri
    private String filtroNome = "";
    private String filtroCitta = "";

    public FornitoreController(FornitoreService fornitoreService, FornitoreView fornitoreView) {
        this.fornitoreService = fornitoreService;
        this.fornitoreView = fornitoreView;
        aggiornaView();
        fornitoreView.setOnNuovoFornitore(this::onNuovoFornitore);
        fornitoreView.setOnModificaFornitore(this::onModificaFornitore);
        fornitoreView.setOnEliminaFornitore(this::onEliminaFornitore);
        // Callback per i filtri
        fornitoreView.setOnTestoRicercaNomeChanged(this::onFiltroNomeChanged);
        fornitoreView.setOnTestoRicercaCittaChanged(this::onFiltroCittaChanged);
    }

    private void onFiltroNomeChanged(String nuovoNome) {
        filtroNome = nuovoNome != null ? nuovoNome : "";
        aggiornaView();
    }

    private void onFiltroCittaChanged(String nuovaCitta) {
        filtroCitta = nuovaCitta != null ? nuovaCitta : "";
        aggiornaView();
    }

    private void aggiornaView() {
        java.util.List<Fornitore> tutti = fornitoreService.getAllFornitori();
        java.util.List<Fornitore> filtrati = tutti.stream()
            .filter(f -> filtroNome.isEmpty() || (f.getNome() != null && f.getNome().toLowerCase().contains(filtroNome.toLowerCase())))
            .filter(f -> filtroCitta.isEmpty() || (f.getIndirizzo() != null && f.getIndirizzo().toLowerCase().contains(filtroCitta.toLowerCase())))
            .toList();
        fornitoreView.setFornitori(filtrati);
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
