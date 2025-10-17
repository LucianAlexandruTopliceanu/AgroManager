package Controller;

import BusinessLogic.Exception.ValidationException;
import BusinessLogic.Exception.BusinessLogicException;
import BusinessLogic.Exception.DataAccessException;
import BusinessLogic.Service.ErrorService;
import BusinessLogic.Service.PiantagioneService;
import BusinessLogic.Service.ZonaService;
import BusinessLogic.Service.PiantaService;
import BusinessLogic.Service.StatoPiantagioneService;
import DomainModel.Piantagione;
import DomainModel.StatoPiantagione;
import DomainModel.Zona;
import DomainModel.Pianta;
import View.PiantagioneDialog;
import View.CambiaStatoDialog;
import View.PiantagioneView;
import View.NotificationHelper;
import java.util.List;

public class PiantagioneController {
    private final PiantagioneService piantagioneService;
    private final ZonaService zonaService;
    private final PiantaService piantaService;
    private final StatoPiantagioneService statoPiantagioneService;
    private final PiantagioneView piantagioneView;

    public PiantagioneController(PiantagioneService piantagioneService, ZonaService zonaService,
                                PiantaService piantaService, StatoPiantagioneService statoPiantagioneService,
                                PiantagioneView piantagioneView) {
        this.piantagioneService = piantagioneService;
        this.zonaService = zonaService;
        this.piantaService = piantaService;
        this.statoPiantagioneService = statoPiantagioneService;
        this.piantagioneView = piantagioneView;

        setupEventHandlers();
        inizializzaView();
    }

    private void setupEventHandlers() {
        piantagioneView.setOnNuovaPiantagione(this::onNuovaPiantagione);
        piantagioneView.setOnModificaPiantagione(this::onModificaPiantagione);
        piantagioneView.setOnEliminaPiantagione(this::onEliminaPiantagione);
        piantagioneView.setOnCambiaStatoPiantagione(this::onCambiaStatoPiantagione);
        piantagioneView.setOnApplicaFiltri(this::onApplicaFiltri);
        piantagioneView.setOnResetFiltri(this::onResetFiltri);
    }

    private void inizializzaView() {
        try {
            inizializzaFiltri();
            aggiornaListaPiantagioni();
        } catch (Exception e) {
            ErrorService.handleException("inizializzazione view", e);
        }
    }

    private void inizializzaFiltri() {
        try {
            List<Zona> zone = zonaService.getAllZone();
            List<Pianta> piante = piantaService.getAllPiante();

            List<String> nomiZone = zone.stream().map(Zona::getNome).toList();
            List<String> descrizioniPiante = piante.stream()
                .map(p -> p.getTipo() + (p.getVarieta() != null ? " - " + p.getVarieta() : ""))
                .toList();

            piantagioneView.setFiltroPiantaItems(descrizioniPiante);
            piantagioneView.setFiltroZonaItems(nomiZone);
        } catch (Exception e) {
            ErrorService.handleException("inizializzazione filtri piantagioni", e);
        }
    }

    private void onApplicaFiltri() {
        try {
            var criteriFiltro = piantagioneView.getCriteriFiltro();
            var piantagioniFiltrate = piantagioneService.getPiantagioniConFiltri(criteriFiltro);
            piantagioneView.setPiantagioni(piantagioniFiltrate);
        } catch (Exception e) {
            ErrorService.handleException("applicazione filtri", e);
        }
    }

    private void onResetFiltri() {
        piantagioneView.resetFiltri();
        aggiornaListaPiantagioni();
    }

    private void aggiornaListaPiantagioni() {
        try {
            var tutte = piantagioneService.getAllPiantagioni();
            piantagioneView.setPiantagioni(tutte);
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("caricamento piantagioni", e);
        }
    }

    public void onNuovaPiantagione() {
        try {
            var zone = zonaService.getAllZone();
            var piante = piantaService.getAllPiante();
            var stati = statoPiantagioneService.getAllStati();

            PiantagioneDialog dialog = new PiantagioneDialog(null, zone, piante, stati);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                Piantagione piantagione = dialog.getPiantagione();
                piantagioneService.aggiungiPiantagione(piantagione);
                NotificationHelper.showSuccess("Operazione completata", "Piantagione aggiunta con successo!");
                aggiornaListaPiantagioni();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("aggiunta piantagione", e);
        }
    }

    public void onModificaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una piantagione da modificare");
            return;
        }

        try {
            var zone = zonaService.getAllZone();
            var piante = piantaService.getAllPiante();
            var stati = statoPiantagioneService.getAllStati();

            PiantagioneDialog dialog = new PiantagioneDialog(selezionata, zone, piante, stati);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                piantagioneService.aggiornaPiantagione(dialog.getPiantagione());
                NotificationHelper.showSuccess("Operazione completata", "Piantagione aggiornata con successo!");
                aggiornaListaPiantagioni();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("aggiornamento piantagione", e);
        }
    }

    public void onEliminaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una piantagione da eliminare");
            return;
        }

        try {
            boolean confermato = piantagioneView.confermaEliminazione(selezionata);
            if (confermato) {
                piantagioneService.cambiaStatoPiantagione(selezionata.getId(), StatoPiantagione.RIMOSSA);
                NotificationHelper.showSuccess("Operazione completata", "Piantagione rimossa con successo!");
                aggiornaListaPiantagioni();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("rimozione piantagione", e);
        }
    }

    public void onCambiaStatoPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una piantagione per cambiare lo stato");
            return;
        }

        try {
            var stati = statoPiantagioneService.getAllStati();
            CambiaStatoDialog dialog = new CambiaStatoDialog(selezionata, stati);
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                StatoPiantagione nuovoStato = dialog.getStatoSelezionato();
                piantagioneService.cambiaStatoPiantagione(selezionata.getId(), nuovoStato.getCodice());
                NotificationHelper.showSuccess("Operazione completata", "Stato piantagione cambiato con successo!");
                aggiornaListaPiantagioni();
            }
        } catch (ValidationException | DataAccessException | BusinessLogicException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("cambio stato piantagione", e);
        }
    }

    public void refreshData() {
        aggiornaListaPiantagioni();
    }
}
