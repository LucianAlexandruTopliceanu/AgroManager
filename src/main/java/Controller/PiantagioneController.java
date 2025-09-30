package Controller;

import BusinessLogic.Service.PiantagioneService;
import BusinessLogic.Service.ZonaService;
import BusinessLogic.Service.PiantaService;
import DomainModel.Piantagione;
import View.PiantagioneDialog;
import View.PiantagioneView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PiantagioneController {
    private final PiantagioneService piantagioneService;
    private final ZonaService zonaService;
    private final PiantaService piantaService;
    private final PiantagioneView piantagioneView;

    // Stato dei filtri
    private String filtroPianta = "";
    private String filtroZona = "";
    private java.time.LocalDate filtroDataDa = null;
    private java.time.LocalDate filtroDataA = null;


    public PiantagioneController(PiantagioneService piantagioneService, ZonaService zonaService, PiantaService piantaService, PiantagioneView piantagioneView) {
        this.piantagioneService = piantagioneService;
        this.zonaService = zonaService;
        this.piantaService = piantaService;
        this.piantagioneView = piantagioneView;
        aggiornaView();
        piantagioneView.setOnNuovaPiantagione(this::onNuovaPiantagione);
        piantagioneView.setOnModificaPiantagione(this::onModificaPiantagione);
        piantagioneView.setOnEliminaPiantagione(this::onEliminaPiantagione);
        // Callback per i filtri
        piantagioneView.setOnFiltroPiantaChanged(this::onFiltroPiantaChanged);
        piantagioneView.setOnFiltroZonaChanged(this::onFiltroZonaChanged);
        piantagioneView.setOnFiltroDataDaChanged(this::onFiltroDataDaChanged);
        piantagioneView.setOnFiltroDataAChanged(this::onFiltroDataAChanged);
    }

    private void onFiltroPiantaChanged(String nuovaPianta) {
        filtroPianta = nuovaPianta != null ? nuovaPianta : "";
        aggiornaView();
    }
    private void onFiltroZonaChanged(String nuovaZona) {
        filtroZona = nuovaZona != null ? nuovaZona : "";
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
        java.util.List<Piantagione> tutte = piantagioneService.getAllPiantagioni();
        java.util.List<Piantagione> filtrate = tutte.stream()
            .filter(p -> filtroPianta.isEmpty() || filtroPianta.equals("Tutte") || (p.getPiantaId() != null && p.getPiantaId().toString().equals(filtroPianta)))
            .filter(p -> filtroZona.isEmpty() || filtroZona.equals("Tutte") || (p.getZonaId() != null && p.getZonaId().toString().equals(filtroZona)))
            .filter(p -> filtroDataDa == null || (p.getMessaADimora() != null && !p.getMessaADimora().isBefore(filtroDataDa)))
            .filter(p -> filtroDataA == null || (p.getMessaADimora() != null && !p.getMessaADimora().isAfter(filtroDataA)))
            .toList();
        piantagioneView.setPiantagioni(filtrate);
    }

    public void onNuovaPiantagione() {
        PiantagioneDialog dialog = new PiantagioneDialog(null, zonaService.getAllZone(), piantaService.getAllPiante());
        dialog.showAndWait();
        if (dialog.isConfermato()) {
            try {
                piantagioneService.aggiungiPiantagione(dialog.getPiantagione());
                aggiornaView();
            } catch (Exception ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void onModificaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata != null) {
            PiantagioneDialog dialog = new PiantagioneDialog(selezionata, zonaService.getAllZone(), piantaService.getAllPiante());
            dialog.showAndWait();
            if (dialog.isConfermato()) {
                try {
                    piantagioneService.aggiornaPiantagione(dialog.getPiantagione());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una piantagione da modificare.");
        }
    }

    private void onEliminaPiantagione() {
        Piantagione selezionata = piantagioneView.getPiantagioneSelezionata();
        if (selezionata != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare la piantagione selezionata?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    piantagioneService.eliminaPiantagione(selezionata.getId());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona una piantagione da eliminare.");
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
