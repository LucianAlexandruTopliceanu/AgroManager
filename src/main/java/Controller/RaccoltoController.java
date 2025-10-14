package Controller;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.RaccoltoService;
import BusinessLogic.Service.PiantagioneService;
import DomainModel.Raccolto;
import DomainModel.Piantagione;
import View.RaccoltoDialog;
import View.RaccoltoView;
import View.NotificationHelper;
import java.util.List;

public class RaccoltoController {
    private final RaccoltoService raccoltoService;
    private final PiantagioneService piantagioneService;
    private final RaccoltoView raccoltoView;

    public RaccoltoController(RaccoltoService raccoltoService, PiantagioneService piantagioneService, RaccoltoView raccoltoView) {
        this.raccoltoService = raccoltoService;
        this.piantagioneService = piantagioneService;
        this.raccoltoView = raccoltoView;

        setupEventHandlers();
        inizializzaView();
    }

    private void setupEventHandlers() {
        raccoltoView.setOnNuovoRaccolto(this::onNuovoRaccolto);
        raccoltoView.setOnModificaRaccolto(this::onModificaRaccolto);
        raccoltoView.setOnEliminaRaccolto(this::onEliminaRaccolto);
        raccoltoView.setOnApplicaFiltri(this::onApplicaFiltri);
        raccoltoView.setOnResetFiltri(this::onResetFiltri);
    }

    private void inizializzaView() {
        try {
            // Inizializza filtri nella view
            inizializzaFiltri();

            // Carica tutti i raccolti
            aggiornaListaRaccolti();

        } catch (Exception e) {
            ErrorService.handleException("inizializzazione view", e);
        }
    }

    private void inizializzaFiltri() {
        try {
            List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
            List<String> nomiPiantagioni = piantagioni.stream()
                .map(p -> p.getId() + " - Zona:" + p.getZonaId())
                .toList();
            raccoltoView.setPiantagioni(nomiPiantagioni);
        } catch (Exception e) {
            ErrorService.handleException("caricamento piantagioni per filtri", e);
        }
    }

    // Event handlers - solo coordinamento
    private void onApplicaFiltri() {
        try {
            // Ottieni i criteri di filtro dalla view
            var criteriFiltro = raccoltoView.getCriteriFiltro();

            // Delega al service il recupero dei dati filtrati
            var raccoltiFiltrati = raccoltoService.getRaccoltiConFiltri(criteriFiltro);

            // Passa i risultati alla view
            raccoltoView.setRaccolti(raccoltiFiltrati);

        } catch (Exception e) {
            ErrorService.handleException("applicazione filtri", e);
        }
    }

    private void onResetFiltri() {
        raccoltoView.resetFiltri();
        aggiornaListaRaccolti();
    }

    private void aggiornaListaRaccolti() {
        try {
            var tutti = raccoltoService.getAllRaccolti();
            raccoltoView.setRaccolti(tutti);
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("caricamento raccolti", e);
        }
    }

    public void onNuovoRaccolto() {
        try {
            var piantagioni = piantagioneService.getAllPiantagioni();
            RaccoltoDialog dialog = new RaccoltoDialog(null, piantagioni);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                Raccolto raccolto = dialog.getRaccolto();
                raccoltoService.aggiungiRaccolto(raccolto);
                NotificationHelper.showSuccess("Operazione completata", "Raccolto aggiunto con successo!");
                aggiornaListaRaccolti();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("aggiunta raccolto", e);
        }
    }

    public void onModificaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona un raccolto da modificare");
            return;
        }

        try {
            var piantagioni = piantagioneService.getAllPiantagioni();
            RaccoltoDialog dialog = new RaccoltoDialog(selezionato, piantagioni);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                raccoltoService.aggiornaRaccolto(dialog.getRaccolto());
                NotificationHelper.showSuccess("Operazione completata", "Raccolto aggiornato con successo!");
                aggiornaListaRaccolti();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("aggiornamento raccolto", e);
        }
    }

    public void onEliminaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona un raccolto da eliminare");
            return;
        }

        try {
            boolean confermato = raccoltoView.confermaEliminazione(selezionato);
            if (confermato) {
                raccoltoService.eliminaRaccolto(selezionato.getId());
                NotificationHelper.showSuccess("Operazione completata", "Raccolto eliminato con successo!");
                aggiornaListaRaccolti();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("eliminazione raccolto", e);
        }
    }

    public void refreshData() {
        aggiornaListaRaccolti();
    }
}
