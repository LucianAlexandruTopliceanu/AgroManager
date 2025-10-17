package Controller;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.FornitoreService;
import DomainModel.Fornitore;
import View.FornitoreDialog;
import View.FornitoreView;
import View.NotificationHelper;

public class FornitoreController {
    private final FornitoreService fornitoreService;
    private final FornitoreView fornitoreView;

    public FornitoreController(FornitoreService fornitoreService, FornitoreView fornitoreView) {
        this.fornitoreService = fornitoreService;
        this.fornitoreView = fornitoreView;

        setupEventHandlers();
        aggiornaListaFornitori();
    }

    private void setupEventHandlers() {
        fornitoreView.setOnNuovoFornitore(this::onNuovoFornitore);
        fornitoreView.setOnModificaFornitore(this::onModificaFornitore);
        fornitoreView.setOnEliminaFornitore(this::onEliminaFornitore);
        fornitoreView.setOnApplicaFiltri(this::onApplicaFiltri);
        fornitoreView.setOnResetFiltri(this::onResetFiltri);
    }

    private void onApplicaFiltri() {
        try {
            var criteriFiltro = fornitoreView.getCriteriFiltro();
            var fornitoriFiltrati = fornitoreService.getFornitoriConFiltri(criteriFiltro);
            fornitoreView.setFornitori(fornitoriFiltrati);
        } catch (Exception e) {
            ErrorService.handleException("applicazione filtri", e);
        }
    }

    private void onResetFiltri() {
        fornitoreView.resetFiltri();
        aggiornaListaFornitori();
    }

    private void aggiornaListaFornitori() {
        try {
            var tutti = fornitoreService.getAllFornitori();
            fornitoreView.setFornitori(tutti);
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("caricamento fornitori", e);
        }
    }

    public void onNuovoFornitore() {
        FornitoreDialog dialog = new FornitoreDialog(new Fornitore());
        dialog.showAndWait();

        if (dialog.isConfermato()) {
            try {
                Fornitore fornitore = dialog.getFornitore();
                fornitoreService.aggiungiFornitore(fornitore);
                NotificationHelper.showSuccess("Operazione completata", "Fornitore aggiunto con successo!");
                aggiornaListaFornitori();
            } catch (ValidationException | DataAccessException | BusinessLogicException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("aggiunta fornitore", e);
            }
        }
    }

    public void onModificaFornitore() {
        Fornitore selezionato = fornitoreView.getFornitoreSelezionato();
        if (selezionato == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona un fornitore da modificare");
            return;
        }

        FornitoreDialog dialog = new FornitoreDialog(selezionato);
        dialog.showAndWait();

        if (dialog.isConfermato()) {
            try {
                fornitoreService.aggiornaFornitore(dialog.getFornitore());
                NotificationHelper.showSuccess("Operazione completata", "Fornitore aggiornato con successo!");
                aggiornaListaFornitori();
            } catch (ValidationException | DataAccessException | BusinessLogicException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("aggiornamento fornitore", e);
            }
        }
    }

    public void onEliminaFornitore() {
        Fornitore selezionato = fornitoreView.getFornitoreSelezionato();
        if (selezionato == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona un fornitore da eliminare");
            return;
        }

        try {
            boolean confermato = fornitoreView.confermaEliminazione(selezionato);
            if (confermato) {
                fornitoreService.eliminaFornitore(selezionato.getId());
                NotificationHelper.showSuccess("Operazione completata", "Fornitore eliminato con successo!");
                aggiornaListaFornitori();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("eliminazione fornitore", e);
        }
    }

    public void refreshData() {
        aggiornaListaFornitori();
    }
}
