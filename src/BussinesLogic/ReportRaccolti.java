package BussinesLogic;

import BussinesLogic.RaccoltoService;
import DomainModel.Raccolto;

import java.util.List;

public class ReportRaccolti implements ReportStrategy<String> {
    private final RaccoltoService raccoltoService;

    public ReportRaccolti(RaccoltoService raccoltoService) {
        this.raccoltoService = raccoltoService;
    }

    @Override
    public String generaReport() {
        List<Raccolto> raccolti = raccoltoService.getAllRaccolti();
        StringBuilder sb = new StringBuilder();
        sb.append("--- Report Raccolti ---\n");
        for (Raccolto r : raccolti) {
            String data = r.getDataRaccolto() != null ? r.getDataRaccolto().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            sb.append("ID: ").append(r.getId())
              .append(", Data: ").append(data)
              .append(", Quantità: ").append(r.getQuantitaKg()).append(" kg")
              .append(", Note: ").append(r.getNote()).append("\n");
        }
        return sb.toString();
    }
}
