package BussinesLogic;

import Controller.RaccoltoController;
import DomainModel.Raccolto;

import java.sql.SQLException;
import java.util.List;

public class ReportRaccolti implements ReportStrategy {
    private final RaccoltoController raccoltoController;

    public ReportRaccolti(RaccoltoController raccoltoController) {
        this.raccoltoController = raccoltoController;
    }

    @Override
    public void generaReport() {
        try {
            List<Raccolto> raccolti = raccoltoController.getAllRaccolti();
            System.out.println("--- Report Raccolti ---");
            for (Raccolto r : raccolti) {
                System.out.println("ID: " + r.getId() + ", Data: " + r.getDataRaccolto() + ", Quantità: " + r.getQuantitaKg() + " kg, Note: " + r.getNote());
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la generazione del report: " + e.getMessage());
        }
    }
}
