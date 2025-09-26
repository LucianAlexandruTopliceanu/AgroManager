package BussinesLogic.Strategy;

import BussinesLogic.Service.RaccoltoService;
import BussinesLogic.Service.PiantagioneService;
import BussinesLogic.Service.ZonaService;
import DomainModel.Raccolto;
import DomainModel.Piantagione;
import DomainModel.Zona;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportStatisticheZona implements ReportStrategy<Map<String, Integer>> {
    private final RaccoltoService raccoltoService;
    private final PiantagioneService piantagioneService;
    private final ZonaService zonaService;

    public ReportStatisticheZona(RaccoltoService raccoltoService, PiantagioneService piantagioneService, ZonaService zonaService) {
        this.raccoltoService = raccoltoService;
        this.piantagioneService = piantagioneService;
        this.zonaService = zonaService;
    }

    @Override
    public Map<String, Integer> generaReport() {
        List<Raccolto> raccolti = raccoltoService.getAllRaccolti();
        List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
        List<Zona> zone = zonaService.getAllZone();
        Map<Integer, String> idToNomeZona = new HashMap<>();
        for (Zona z : zone) idToNomeZona.put(z.getId(), z.getNome());
        Map<String, Integer> raccoltoPerZona = new HashMap<>();
        for (Raccolto r : raccolti) {
            Piantagione p = piantagioni.stream().filter(pg -> pg.getId().equals(r.getPiantagioneId())).findFirst().orElse(null);
            if (p != null) {
                String nomeZona = idToNomeZona.getOrDefault(p.getZonaId(), "Sconosciuta");
                raccoltoPerZona.merge(nomeZona, r.getQuantitaKgInt(), Integer::sum);
            }
        }
        return raccoltoPerZona;
    }
}
