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
import View.PiantagioneDialog;
import View.CambiaStatoDialog;
import View.PiantagioneView;
import View.NotificationHelper;

public class PiantagioneController {
    private final PiantagioneService piantagioneService;
    private final ZonaService zonaService;
    private final PiantaService piantaService;
    private final StatoPiantagioneService statoPiantagioneService;
    private final PiantagioneView piantagioneView;

    // Stato dei filtri
    private String filtroPianta = "";
    private String filtroZona = "";
    private String filtroStato = "";
    private java.time.LocalDate filtroDataDa = null;
    private java.time.LocalDate filtroDataA = null;

    public PiantagioneController(PiantagioneService piantagioneService, ZonaService zonaService,
                                PiantaService piantaService, StatoPiantagioneService statoPiantagioneService,
                                PiantagioneView piantagioneView) {
        this.piantagioneService = piantagioneService;
        this.zonaService = zonaService;
        this.piantaService = piantaService;
        this.statoPiantagioneService = statoPiantagioneService;
        this.piantagioneView = piantagioneView;

        setupEventHandlers();
        inizializzaFiltri();
        aggiornaView();
    }

    private void setupEventHandlers() {
        piantagioneView.setOnNuovaPiantagione(this::onNuovaPiantagione);
        piantagioneView.setOnModificaPiantagione(this::onModificaPiantagione);
        piantagioneView.setOnEliminaPiantagione(this::onEliminaPiantagione);
        piantagioneView.setOnCambiaStatoPiantagione(this::onCambiaStatoPiantagione);

        // Callback per i filtri
        piantagioneView.setOnFiltroPiantaChanged(this::onFiltroPiantaChanged);
        piantagioneView.setOnFiltroZonaChanged(this::onFiltroZonaChanged);
        piantagioneView.setOnFiltroStatoChanged(this::onFiltroStatoChanged);
        piantagioneView.setOnFiltroDataDaChanged(this::onFiltroDataDaChanged);
        piantagioneView.setOnFiltroDataAChanged(this::onFiltroDataAChanged);
    }

    private void inizializzaFiltri() {
        try {
            // Popola filtro stati
            java.util.List<StatoPiantagione> stati = statoPiantagioneService.getAllStati();
            java.util.List<String> statiNomi = stati.stream()
                .map(StatoPiantagione::getDescrizione)
                .collect(java.util.stream.Collectors.toList());
            statiNomi.add(0, "Tutti");
            piantagioneView.setFiltroStatoItems(statiNomi);

        } catch (Exception e) {
            ErrorService.handleException("inizializzazione filtri", e);
        }
    }

    private void onFiltroPiantaChanged(String nuovaPianta) {
        filtroPianta = nuovaPianta != null ? nuovaPianta : "";
        aggiornaView();
    }

    private void onFiltroZonaChanged(String nuovaZona) {
        filtroZona = nuovaZona != null ? nuovaZona : "";
        aggiornaView();
    }

    private void onFiltroStatoChanged(String nuovoStato) {
        filtroStato = nuovoStato != null ? nuovoStato : "";
        aggiornaView();
    }

    private void onFiltroDataDaChanged(java.time.LocalDate nuovaDataDa) {
        filtroDataDa = nuovaDataDa;
        aggiornaView();
    }

    private void onFiltroDataAChanged(java.time.LocalDate nuovaDataA) {
        filtroDataA = nuovaDataA;
        aggiornaView();
    }

    private void aggiornaView() {
        try {
            java.util.List<Piantagione> tutte = piantagioneService.getAllPiantagioni();

            // Applica i filtri
            java.util.List<Piantagione> filtrate = tutte.stream()
                .filter(p -> filtroPianta.isEmpty() || filtroPianta.equals("Tutte") ||
                            (p.getPiantaId() != null && p.getPiantaId().toString().equals(filtroPianta)))
                .filter(p -> filtroZona.isEmpty() || filtroZona.equals("Tutte") ||
                            (p.getZonaId() != null && p.getZonaId().toString().equals(filtroZona)))
                .filter(p -> filtroStato.isEmpty() || filtroStato.equals("Tutti") ||
                            (p.getStatoPiantagione() != null && p.getStatoPiantagione().getDescrizione().equals(filtroStato)))
                .filter(p -> filtroDataDa == null ||
                            (p.getMessaADimora() != null && !p.getMessaADimora().isBefore(filtroDataDa)))
                .filter(p -> filtroDataA == null ||
                            (p.getMessaADimora() != null && !p.getMessaADimora().isAfter(filtroDataA)))
                .toList();

            piantagioneView.setPiantagioni(filtrate);

        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        } catch (Exception e) {
            ErrorService.handleException("caricamento piantagioni", e);
        }
    }

    public void onNuovaPiantagione() {
        try {
            PiantagioneDialog dialog = new PiantagioneDialog(
                null,
                zonaService.getAllZone(),
                piantaService.getAllPiante(),
                statoPiantagioneService.getAllStati()
            );
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                Piantagione piantagione = dialog.getPiantagione();
                try {
                    piantagioneService.aggiungiPiantagione(piantagione);
                    NotificationHelper.showSuccess("Operazione completata", "Piantagione aggiunta con successo!");
                    aggiornaView();

                } catch (ValidationException | DataAccessException e) {
                    ErrorService.handleException(e);
                } catch (Exception e) {
                    ErrorService.handleException("aggiunta piantagione", e);
                }
            }
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        }
    }

    public void onModificaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una piantagione da modificare");
            return;
        }

        try {
            PiantagioneDialog dialog = new PiantagioneDialog(
                selezionata,
                zonaService.getAllZone(),
                piantaService.getAllPiante(),
                statoPiantagioneService.getAllStati()
            );
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                try {
                    piantagioneService.aggiornaPiantagione(dialog.getPiantagione());
                    NotificationHelper.showSuccess("Operazione completata", "Piantagione aggiornata con successo!");
                    aggiornaView();

                } catch (ValidationException | DataAccessException e) {
                    ErrorService.handleException(e);
                } catch (Exception e) {
                    ErrorService.handleException("aggiornamento piantagione", e);
                }
            }
        } catch (DataAccessException e) {
            ErrorService.handleException(e);
        }
    }

    public void onCambiaStatoPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una piantagione per cambiare lo stato");
            return;
        }

        try {
            CambiaStatoDialog dialog = new CambiaStatoDialog(
                selezionata,
                statoPiantagioneService.getAllStati()
            );
            dialog.showAndWait();

            if (dialog.isConfermato()) {
                try {
                    // Aggiorna la piantagione con il nuovo stato
                    selezionata.setStatoPiantagione(dialog.getStatoSelezionato());
                    selezionata.setIdStatoPiantagione(dialog.getStatoSelezionato().getId());

                    piantagioneService.aggiornaPiantagione(selezionata);

                    String messaggio = "Stato cambiato in: " + dialog.getStatoSelezionato().getDescrizione();
                    if (!dialog.getNote().isEmpty()) {
                        messaggio += "\nNote: " + dialog.getNote();
                    }

                    NotificationHelper.showSuccess("Stato aggiornato", messaggio);
                    aggiornaView();

                } catch (ValidationException | DataAccessException e) {
                    ErrorService.handleException(e);
                } catch (Exception e) {
                    ErrorService.handleException("cambio stato piantagione", e);
                }
            }
        } catch (Exception e) {
            ErrorService.handleException("apertura dialog cambio stato", e);
        }
    }

    public void onEliminaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata == null) {
            NotificationHelper.showWarning("Selezione richiesta", "Seleziona una piantagione da eliminare");
            return;
        }

        if (NotificationHelper.confirmCriticalOperation("Eliminazione Piantagione",
                "Piantagione ID: " + selezionata.getId())) {
            try {
                piantagioneService.eliminaPiantagione(selezionata.getId());
                NotificationHelper.showSuccess("Operazione completata", "Piantagione eliminata con successo!");
                aggiornaView();

            } catch (ValidationException | DataAccessException e) {
                ErrorService.handleException(e);
            } catch (Exception e) {
                ErrorService.handleException("eliminazione piantagione", e);
            }
        }
    }
}
