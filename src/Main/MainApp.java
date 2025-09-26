package Main;

import Controller.ZonaController;
import Controller.FornitoreController;
import Controller.PiantaController;
import Controller.PiantagioneController;
import Controller.RaccoltoController;
import BussinesLogic.Service.ZonaService;
import BussinesLogic.Service.FornitoreService;
import BussinesLogic.Service.PiantaService;
import BussinesLogic.Service.PiantagioneService;
import BussinesLogic.Service.RaccoltoService;
import BussinesLogic.Strategy.ReportRaccolti;
import BussinesLogic.Strategy.ReportStrategy;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import View.ZonaView;
import View.FornitoreView;
import View.PiantaView;
import View.PiantagioneView;
import View.RaccoltoView;
import javafx.scene.chart.*;
import java.util.List;

import java.util.HashMap;
import DomainModel.Raccolto;
import DomainModel.Piantagione;
import DomainModel.Pianta;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AgroManager");
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                creaTabZone(),
                creaTabFornitori(),
                creaTabPiante(),
                creaTabPiantagioni(),
                creaTabReportRaccolti(),
                creaTabRaccolti(),
                creaTabStatistiche()
        );
        Scene scene = new Scene(tabPane, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab creaTabZone() {
        ZonaView zonaView = new ZonaView();
        ZonaService zonaService = new ZonaService(ORM.DAOFactory.getZonaDAO());
        new ZonaController(zonaService, zonaView);
        Tab tab = new Tab("Zone", zonaView);
        tab.setClosable(false);
        return tab;
    }

    private Tab creaTabFornitori() {
        FornitoreView fornitoreView = new FornitoreView();
        FornitoreService fornitoreService = new FornitoreService(ORM.DAOFactory.getFornitoreDAO());
        new FornitoreController(fornitoreService, fornitoreView);
        Tab tab = new Tab("Fornitori", fornitoreView);
        tab.setClosable(false);
        return tab;
    }

    private Tab creaTabPiante() {
        PiantaView piantaView = new PiantaView();
        PiantaService piantaService = new PiantaService(ORM.DAOFactory.getPiantaDAO());
        new PiantaController(piantaService, piantaView);
        Tab tab = new Tab("Piante", piantaView);
        tab.setClosable(false);
        return tab;
    }

    private Tab creaTabPiantagioni() {
        PiantagioneView piantagioneView = new PiantagioneView();
        PiantagioneService piantagioneService = new PiantagioneService(ORM.DAOFactory.getPiantagioneDAO());
        ZonaService zonaService = new ZonaService(ORM.DAOFactory.getZonaDAO());
        PiantaService piantaService = new PiantaService(ORM.DAOFactory.getPiantaDAO());
        new PiantagioneController(piantagioneService, zonaService, piantaService, piantagioneView);
        Tab tab = new Tab("Piantagioni", piantagioneView);
        tab.setClosable(false);
        return tab;
    }

    private Tab creaTabReportRaccolti() {
        Tab tab = new Tab("Report Raccolti");
        tab.setClosable(false);
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        Button aggiornaBtn = new Button("Genera Report");
        RaccoltoService raccoltoService = new RaccoltoService(ORM.DAOFactory.getRaccoltoDAO());
        aggiornaBtn.setOnAction(e -> {
            ReportStrategy<String> strategy = new ReportRaccolti(raccoltoService);
            String report = strategy.generaReport();
            reportArea.setText(report);
        });
        aggiornaBtn.fire();
        VBox vbox = new VBox(10, aggiornaBtn, reportArea);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);
        return tab;
    }

    private Tab creaTabRaccolti() {
        RaccoltoView raccoltoView = new RaccoltoView();
        RaccoltoService raccoltoService = new RaccoltoService(ORM.DAOFactory.getRaccoltoDAO());
        PiantagioneService piantagioneService = new PiantagioneService(ORM.DAOFactory.getPiantagioneDAO());
        new RaccoltoController(raccoltoService, piantagioneService, raccoltoView);
        Tab tab = new Tab("Raccolti", raccoltoView);
        tab.setClosable(false);
        return tab;
    }

    private Tab creaTabStatistiche() {
        Tab tab = new Tab("Statistiche");
        tab.setClosable(false);
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        // Statistica 1: Quantità raccolta totale per pianta
        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis yAxis1 = new NumberAxis();
        BarChart<String, Number> raccoltoPerPiantaChart = new BarChart<>(xAxis1, yAxis1);
        raccoltoPerPiantaChart.setTitle("Quantità raccolta totale per Pianta");
        xAxis1.setLabel("Pianta");
        yAxis1.setLabel("Quantità (kg)");
        XYChart.Series<String, Number> data1 = new XYChart.Series<>();
        try {
            RaccoltoService raccoltoService = new RaccoltoService(ORM.DAOFactory.getRaccoltoDAO());
            PiantagioneService piantagioneService = new PiantagioneService(ORM.DAOFactory.getPiantagioneDAO());
            PiantaService piantaService = new PiantaService(ORM.DAOFactory.getPiantaDAO());
            List<Raccolto> raccolti = raccoltoService.getAllRaccolti();
            List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
            List<Pianta> piante = piantaService.getAllPiante();
            HashMap<Integer, Integer> raccoltoPerPianta = new HashMap<>();
            for (Raccolto raccolto : raccolti) {
                Piantagione piantagione = piantagioni.stream().filter(p -> p.getId().equals(raccolto.getPiantagioneId())).findFirst().orElse(null);
                if (piantagione != null) {
                    raccoltoPerPianta.merge(piantagione.getPiantaId(), raccolto.getQuantitaKgInt(), Integer::sum);
                }
            }
            for (Pianta pianta : piante) {
                int qta = raccoltoPerPianta.getOrDefault(pianta.getId(), 0);
                data1.getData().add(new XYChart.Data<>(pianta.getTipo() + " - " + pianta.getVarieta(), qta));
            }
        } catch (Exception e) {
            // errore
        }
        raccoltoPerPiantaChart.getData().add(data1);
        // Statistica 2: Quantità raccolta totale per zona
        CategoryAxis xAxis2 = new CategoryAxis();
        NumberAxis yAxis2 = new NumberAxis();
        BarChart<String, Number> raccoltoPerZonaChart = new BarChart<>(xAxis2, yAxis2);
        raccoltoPerZonaChart.setTitle("Quantità raccolta totale per Zona");
        xAxis2.setLabel("Zona");
        yAxis2.setLabel("Quantità (kg)");
        XYChart.Series<String, Number> data2 = new XYChart.Series<>();
        try {
            RaccoltoService raccoltoService = new RaccoltoService(ORM.DAOFactory.getRaccoltoDAO());
            PiantagioneService piantagioneService = new PiantagioneService(ORM.DAOFactory.getPiantagioneDAO());
            ZonaService zonaService = new ZonaService(ORM.DAOFactory.getZonaDAO());
            List<Raccolto> raccolti = raccoltoService.getAllRaccolti();
            List<Piantagione> piantagioni = piantagioneService.getAllPiantagioni();
            List<DomainModel.Zona> zone = zonaService.getAllZone();
            HashMap<Integer, Integer> raccoltoPerZona = new HashMap<>();
            for (Raccolto raccolto : raccolti) {
                Piantagione piantagione = piantagioni.stream().filter(p -> p.getId().equals(raccolto.getPiantagioneId())).findFirst().orElse(null);
                if (piantagione != null) {
                    raccoltoPerZona.merge(piantagione.getZonaId(), raccolto.getQuantitaKgInt(), Integer::sum);
                }
            }
            for (DomainModel.Zona zona : zone) {
                int qta = raccoltoPerZona.getOrDefault(zona.getId(), 0);
                data2.getData().add(new XYChart.Data<>(zona.getNome(), qta));
            }
        } catch (Exception e) {
            // errore
        }
        raccoltoPerZonaChart.getData().add(data2);
        // Statistica 3: Zone più produttive (top 3)
        Label topZoneLabel = new Label();
        try {
            ZonaService zonaService = new ZonaService(ORM.DAOFactory.getZonaDAO());
            List<DomainModel.Zona> zone = zonaService.getAllZone();
            java.util.List<XYChart.Data<String, Number>> zoneList = new java.util.ArrayList<>(data2.getData());
            zoneList.sort((a, b) -> Integer.compare(b.getYValue().intValue(), a.getYValue().intValue()));
            StringBuilder sb = new StringBuilder("Zone più produttive (top 3):\n");
            for (int i = 0; i < Math.min(3, zoneList.size()); i++) {
                sb.append((i+1) + ". " + zoneList.get(i).getXValue() + " - " + zoneList.get(i).getYValue() + " kg\n");
            }
            topZoneLabel.setText(sb.toString());
        } catch (Exception e) {
            // errore
        }
        // Statistica 4: Numero totale di piantagioni, piante, zone, raccolti, fornitori
        Label totaliLabel = new Label();
        try {
            ZonaService zonaService = new ZonaService(ORM.DAOFactory.getZonaDAO());
            PiantaService piantaService = new PiantaService(ORM.DAOFactory.getPiantaDAO());
            PiantagioneService piantagioneService = new PiantagioneService(ORM.DAOFactory.getPiantagioneDAO());
            RaccoltoService raccoltoService = new RaccoltoService(ORM.DAOFactory.getRaccoltoDAO());
            FornitoreService fornitoreService = new FornitoreService(ORM.DAOFactory.getFornitoreDAO());
            int nZone = zonaService.getAllZone().size();
            int nPiante = piantaService.getAllPiante().size();
            int nPiantagioni = piantagioneService.getAllPiantagioni().size();
            int nRaccolti = raccoltoService.getAllRaccolti().size();
            int nFornitori = fornitoreService.getAllFornitori().size();
            totaliLabel.setText("Zone: " + nZone + " | Piante: " + nPiante + " | Piantagioni: " + nPiantagioni + " | Raccolti: " + nRaccolti + " | Fornitori: " + nFornitori);
        } catch (Exception e) {
            // errore
        }
        root.getChildren().addAll(totaliLabel, raccoltoPerPiantaChart, raccoltoPerZonaChart, topZoneLabel);
        tab.setContent(new ScrollPane(root));
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
