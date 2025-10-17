package Controller;

import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.PiantaService;
import BusinessLogic.Service.FornitoreService;
import View.PiantaView;
import View.PiantaDialog;
import View.NotificationHelper;
import DomainModel.Pianta;
import DomainModel.Fornitore;
import java.util.List;

public class PiantaController {
    private final PiantaService piantaService;
    private final FornitoreService fornitoreService;
    private final PiantaView piantaView;

    public PiantaController(PiantaService piantaService, FornitoreService fornitoreService, PiantaView piantaView) {
        this.piantaService = piantaService;
        this.fornitoreService = fornitoreService;
        this.piantaView = piantaView;

        setupEventHandlers();
        inizializzaFiltri();
        aggiornaPiante();
    }

    private void setupEventHandlers() {
        piantaView.setOnNuovaPianta(this::onNuovaPianta);
        piantaView.setOnModificaPianta(this::onModificaPianta);
        piantaView.setOnEliminaPianta(this::onEliminaPianta);
        piantaView.setOnApplicaFiltri(this::onApplicaFiltri);
        piantaView.setOnResetFiltri(this::onResetFiltri);
    }

    private void inizializzaFiltri() {
        try {
            List<Fornitore> fornitori = fornitoreService.getAllFornitori();
            List<String> descrizioniFornitori = fornitori.stream()
                .map(f -> f.getNome() + (f.getIndirizzo() != null ? " - " + f.getIndirizzo() : ""))
                .toList();
            piantaView.setFornitori(descrizioniFornitori);
        } catch (Exception e) {
            ErrorService.handleException("caricamento fornitori per filtri", e);
        }
    }

    private void onApplicaFiltri() {
        try {
            var criteriFiltro = piantaView.getCriteriFiltro();
            var pianteFiltrate = piantaService.getPianteConFiltri(criteriFiltro);
            piantaView.setPiante(pianteFiltrate);
        } catch (Exception e) {
            ErrorService.handleException("applicazione filtri", e);
        }
    }

    private void onResetFiltri() {
        piantaView.resetFiltri();
        aggiornaPiante();
    }

    private void aggiornaPiante() {
        try {
            var tutte = piantaService.getAllPiante();
            piantaView.setPiante(tutte);
        } catch (Exception e) {
            ErrorService.handleException("caricamento piante", e);
        }
    }

    public void onNuovaPianta() {
        try {
            List<Fornitore> fornitori = fornitoreService.getAllFornitori();

            if (fornitori.isEmpty()) {
                NotificationHelper.showWarning("Nessun fornitore disponibile",
                    "Devi prima creare almeno un fornitore prima di aggiungere una pianta.");
                return;
            }

            PiantaDialog dialog = new PiantaDialog(new Pianta(), fornitori);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                Pianta pianta = dialog.getPianta();
                piantaService.aggiungiPianta(pianta);
                NotificationHelper.showSuccess("Operazione completata", "Pianta aggiunta con successo!");
                aggiornaPiante();
            }
        } catch (Exception e) {
            ErrorService.handleException("aggiunta pianta", e);
        }
    }

    public void onModificaPianta() {
        Pianta selezionata = piantaView.getPiantaSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una pianta da modificare");
            return;
        }

        try {
            List<Fornitore> fornitori = fornitoreService.getAllFornitori();
            PiantaDialog dialog = new PiantaDialog(selezionata, fornitori);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                piantaService.aggiornaPianta(dialog.getPianta());
                NotificationHelper.showSuccess("Operazione completata", "Pianta aggiornata con successo!");
                aggiornaPiante();
            }
        } catch (Exception e) {
            ErrorService.handleException("aggiornamento pianta", e);
        }
    }

    public void onEliminaPianta() {
        Pianta selezionata = piantaView.getPiantaSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una pianta da eliminare");
            return;
        }

        try {
            boolean confermato = piantaView.confermaEliminazione(selezionata);
            if (confermato) {
                piantaService.eliminaPianta(selezionata.getId());
                NotificationHelper.showSuccess("Operazione completata", "Pianta eliminata con successo!");
                aggiornaPiante();
            }
        } catch (Exception e) {
            ErrorService.handleException("eliminazione pianta", e);
        }
    }
}
