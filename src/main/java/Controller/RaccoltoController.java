package Controller;

import BusinessLogic.Service.RaccoltoService;
import BusinessLogic.Service.PiantagioneService;
import DomainModel.Raccolto;
import DomainModel.Piantagione;
import View.RaccoltoDialog;
import View.RaccoltoView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.List;

public class RaccoltoController {
    private final RaccoltoService raccoltoService;
    private final PiantagioneService piantagioneService;
    private final RaccoltoView raccoltoView;

    // Stato dei filtri
    private String filtroPiantagione = "Tutte";
    private java.time.LocalDate filtroDataDa = null;
    private java.time.LocalDate filtroDataA = null;
    private double filtroQuantitaMin = 0.0;
    private double filtroQuantitaMax = 1000.0;

    public RaccoltoController(RaccoltoService raccoltoService, PiantagioneService piantagioneService, RaccoltoView raccoltoView) {
        this.raccoltoService = raccoltoService;
        this.piantagioneService = piantagioneService;
        this.raccoltoView = raccoltoView;
        aggiornaView();
        raccoltoView.setOnNuovoRaccolto(this::onNuovoRaccolto);
        raccoltoView.setOnModificaRaccolto(this::onModificaRaccolto);
        raccoltoView.setOnEliminaRaccolto(this::onEliminaRaccolto);
        // Callback per i filtri
        raccoltoView.setOnFiltroPiantagioneChanged(this::onFiltroPiantagioneChanged);
        raccoltoView.setOnFiltroDataDaChanged(this::onFiltroDataDaChanged);
        raccoltoView.setOnFiltroDataAChanged(this::onFiltroDataAChanged);
        raccoltoView.setOnFiltroQuantitaMinChanged(this::onFiltroQuantitaMinChanged);
        raccoltoView.setOnFiltroQuantitaMaxChanged(this::onFiltroQuantitaMaxChanged);
    }

    private void onFiltroPiantagioneChanged(String nuovaPiantagione) {
        filtroPiantagione = nuovaPiantagione != null ? nuovaPiantagione : "Tutte";
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
    private void onFiltroQuantitaMinChanged(Double nuovaMin) {
        filtroQuantitaMin = nuovaMin != null ? nuovaMin : 0.0;
        aggiornaView();
    }
    private void onFiltroQuantitaMaxChanged(Double nuovaMax) {
        filtroQuantitaMax = nuovaMax != null ? nuovaMax : 1000.0;
        aggiornaView();
    }

    private void aggiornaView() {
        java.util.List<Raccolto> tutti = raccoltoService.getAllRaccolti();
        java.util.List<Raccolto> filtrati = tutti.stream()
            .filter(r -> filtroPiantagione.equals("Tutte") || (r.getPiantagioneId() != null && r.getPiantagioneId().toString().equals(filtroPiantagione)))
            .filter(r -> filtroDataDa == null || (r.getDataRaccolto() != null && !r.getDataRaccolto().isBefore(filtroDataDa)))
            .filter(r -> filtroDataA == null || (r.getDataRaccolto() != null && !r.getDataRaccolto().isAfter(filtroDataA)))
            .filter(r -> {
                double q = r.getQuantitaKg() != null ? r.getQuantitaKg().doubleValue() : 0.0;
                return q >= filtroQuantitaMin && q <= filtroQuantitaMax;
            })
            .toList();
        raccoltoView.setRaccolti(filtrati);
    }

    public void onNuovoRaccolto() {
        List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
        RaccoltoDialog dialog = new RaccoltoDialog(null, piantagioni);
        dialog.showAndWait();
        if (dialog.isConfermato()) {
            try {
                raccoltoService.aggiungiRaccolto(dialog.getRaccolto());
                aggiornaView();
            } catch (Exception ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void onModificaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato != null) {
            List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
            RaccoltoDialog dialog = new RaccoltoDialog(selezionato, piantagioni);
            dialog.showAndWait();
            if (dialog.isConfermato()) {
                try {
                    raccoltoService.aggiornaRaccolto(dialog.getRaccolto());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona un raccolto da modificare.");
        }
    }

    private void onEliminaRaccolto() {
        Raccolto selezionato = raccoltoView.getRaccoltoSelezionato();
        if (selezionato != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminare il raccolto selezionato?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    raccoltoService.eliminaRaccolto(selezionato.getId());
                    aggiornaView();
                } catch (Exception ex) {
                    mostraErrore(ex.getMessage());
                }
            }
        } else {
            mostraErrore("Seleziona un raccolto da eliminare.");
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
