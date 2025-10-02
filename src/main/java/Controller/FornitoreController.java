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

    // Stato dei filtri
    private String filtroNome = "";
    private String filtroCitta = "";

    public FornitoreController(FornitoreService fornitoreService, FornitoreView fornitoreView) {
        this.fornitoreService = fornitoreService;
        this.fornitoreView = fornitoreView;

        // Inizializza i listener per la gestione errori
        setupEventHandlers();
        aggiornaView();
    }

    private void setupEventHandlers() {
        fornitoreView.setOnNuovoFornitore(this::onNuovoFornitore);
        fornitoreView.setOnModificaFornitore(this::onModificaFornitore);
        fornitoreView.setOnEliminaFornitore(this::onEliminaFornitore);

        // Callback per i filtri
        fornitoreView.setOnTestoRicercaNomeChanged(this::onFiltroNomeChanged);
        fornitoreView.setOnTestoRicercaCittaChanged(this::onFiltroCittaChanged);

        // Callback per aggiornamento dati
        fornitoreView.setOnAggiornaFornitori(v -> aggiornaView());
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
        try {
            java.util.List<Fornitore> tutti = fornitoreService.getAllFornitori();

            // Applica i filtri
            java.util.List<Fornitore> filtrati = tutti.stream()
                .filter(f -> filtroNome.isEmpty() ||
                            f.getNome().toLowerCase().contains(filtroNome.toLowerCase()))
                .filter(f -> filtroCitta.isEmpty() ||
                            f.getIndirizzo().toLowerCase().contains(filtroCitta.toLowerCase()))
                .toList();

            fornitoreView.setFornitori(filtrati);

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
            Fornitore fornitore = dialog.getFornitore();
            try {
                fornitoreService.aggiungiFornitore(fornitore);
                NotificationHelper.showSuccess("Operazione completata", "Fornitore aggiunto con successo!");
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
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
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
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

        if (NotificationHelper.confirmCriticalOperation("Eliminazione Fornitore",
                "Fornitore: " + selezionato.getNome())) {
            try {
                fornitoreService.eliminaFornitore(selezionato.getId());
                NotificationHelper.showSuccess("Operazione completata", "Fornitore eliminato con successo!");
                aggiornaView();

            } catch (ValidationException | BusinessLogicException | DataAccessException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("eliminazione fornitore", e);
            }
        }
    }
}
