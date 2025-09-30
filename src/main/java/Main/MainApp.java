package Main;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Service.*;
import Controller.*;
import ORM.DAOFactory;
import View.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class MainApp extends Application {

    // Services
    private ZonaService zonaService;
    private FornitoreService fornitoreService;
    private PiantaService piantaService;
    private PiantagioneService piantagioneService;
    private RaccoltoService raccoltoService;
    private BusinessLogic businessLogic;

    // Controllers
    private ZonaController zonaController;
    private FornitoreController fornitoreController;
    private PiantaController piantaController;
    private PiantagioneController piantagioneController;
    private RaccoltoController raccoltoController;

    // Views
    private ZonaView zonaView;
    private FornitoreView fornitoreView;
    private PiantaView piantaView;
    private PiantagioneView piantagioneView;
    private RaccoltoView raccoltoView;
    private DataProcessingView dataProcessingView;

    @Override
    public void start(Stage primaryStage) {
        initializeServices();
        initializeViews();
        initializeControllers();

        primaryStage.setTitle("🌱 AgroManager - Sistema di Gestione Agricola");
        primaryStage.setMaximized(true);

        // Crea layout principale con menu laterale
        BorderPane mainLayout = new BorderPane();

        // Area contenuto principale
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(
                creaTabDashboard(tabPane),
                creaTabZone(),
                creaTabFornitori(),
                creaTabPiante(),
                creaTabPiantagioni(),
                creaTabRaccolti(),
                creaTabElaborazioniDati()
        );

        // Menu laterale con riferimento al TabPane
        VBox menuLaterale = creaMenuLaterale(tabPane);
        mainLayout.setLeft(menuLaterale);

        mainLayout.setCenter(tabPane);

        // Barra di stato
        HBox barraStato = creaBarraStato();
        mainLayout.setBottom(barraStato);

        Scene scene = new Scene(mainLayout, 1400, 900);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        } catch (Exception e) {
            // CSS file non trovato, continua senza stili
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeServices() {
        zonaService = new ZonaService(DAOFactory.getZonaDAO());
        fornitoreService = new FornitoreService(DAOFactory.getFornitoreDAO());
        piantaService = new PiantaService(DAOFactory.getPiantaDAO());
        piantagioneService = new PiantagioneService(DAOFactory.getPiantagioneDAO());
        raccoltoService = new RaccoltoService(DAOFactory.getRaccoltoDAO());
        businessLogic = new BusinessLogic();
    }

    private void initializeViews() {
        zonaView = new ZonaView();
        fornitoreView = new FornitoreView();
        piantaView = new PiantaView();
        piantagioneView = new PiantagioneView();
        raccoltoView = new RaccoltoView();
        dataProcessingView = new DataProcessingView(businessLogic);
    }

    private void initializeControllers() {
        zonaController = new ZonaController(zonaService, zonaView);
        fornitoreController = new FornitoreController(fornitoreService, fornitoreView);
        piantaController = new PiantaController(piantaService, piantaView);
        piantagioneController = new PiantagioneController(piantagioneService, zonaService, piantaService, piantagioneView);
        raccoltoController = new RaccoltoController(raccoltoService, piantagioneService, raccoltoView);
    }

    private VBox creaMenuLaterale(TabPane tabPane) {
        VBox menu = new VBox(15);
        menu.setPadding(new Insets(20));
        menu.setStyle("-fx-background-color: #2C3E50; -fx-min-width: 200px;");

        Label titolo = new Label("🌱 AgroManager");
        titolo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button dashboardBtn = creaMenuButton("📊 Dashboard", "Panoramica generale");
        dashboardBtn.setOnAction(e -> tabPane.getSelectionModel().select(0));

        Button zoneBtn = creaMenuButton("🗺️ Zone", "Gestione zone agricole");
        zoneBtn.setOnAction(e -> tabPane.getSelectionModel().select(1));

        Button fornitoriBtn = creaMenuButton("🏢 Fornitori", "Gestione fornitori");
        fornitoriBtn.setOnAction(e -> tabPane.getSelectionModel().select(2));

        Button pianteBtn = creaMenuButton("🌿 Piante", "Catalogo piante");
        pianteBtn.setOnAction(e -> tabPane.getSelectionModel().select(3));

        Button piantagioniBtn = creaMenuButton("🌱 Piantagioni", "Piantagioni attive");
        piantagioniBtn.setOnAction(e -> tabPane.getSelectionModel().select(4));

        Button raccoltiBtn = creaMenuButton("🥕 Raccolti", "Registro raccolti");
        raccoltiBtn.setOnAction(e -> tabPane.getSelectionModel().select(5));

        Button elaborazioniBtn = creaMenuButton("📈 Analisi", "Elaborazioni e report");
        elaborazioniBtn.setOnAction(e -> tabPane.getSelectionModel().select(6));

        menu.getChildren().addAll(
                titolo,
                new Separator(),
                dashboardBtn, zoneBtn, fornitoriBtn,
                pianteBtn, piantagioniBtn, raccoltiBtn,
                new Separator(),
                elaborazioniBtn
        );

        return menu;
    }

    private Button creaMenuButton(String testo, String tooltip) {
        Button btn = new Button(testo);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-alignment: center-left; -fx-pref-width: 160px;");
        btn.setTooltip(new Tooltip(tooltip));

        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-background-color: #34495E;"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-background-color: #34495E;", "")));

        return btn;
    }

    private Tab creaTabDashboard(TabPane tabPane) {
        Tab tab = new Tab("📊 Dashboard");

        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(20));

        // Carte statistiche principali
        HBox carteStatsBox = new HBox(20);
        carteStatsBox.getChildren().addAll(
                creaCartaStatistica("Zone Attive", "12", "#3498DB"),
                creaCartaStatistica("Piantagioni", "45", "#2ECC71"),
                creaCartaStatistica("Raccolti Mese", "23", "#E74C3C"),
                creaCartaStatistica("Produzione Tot.", "1,247 kg", "#F39C12")
        );

        // Accesso rapido
        Label accessoRapidoLabel = new Label("🚀 Accesso Rapido");
        accessoRapidoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox accessoRapidoBox = new HBox(15);

        Button nuovaZonaBtn = creaButtoneAzione("➕ Nuova Zona", "#3498DB");
        nuovaZonaBtn.setOnAction(e -> {
            tabPane.getSelectionModel().select(1); // Vai al tab Zone
            // Simula click su "Nuova Zona" - il controller gestirà l'apertura del dialog
            //TODO:zonaController.nuovaZona();
        });

        Button nuovaPiantagioneBtn = creaButtoneAzione("🌱 Nuova Piantagione", "#2ECC71");
        nuovaPiantagioneBtn.setOnAction(e -> {
            tabPane.getSelectionModel().select(4); // Vai al tab Piantagioni
            //TODO:piantagioneController.nuovaPiantagione();
        });

        Button nuovoRaccoltoBtn = creaButtoneAzione("📝 Nuovo Raccolto", "#E74C3C");
        nuovoRaccoltoBtn.setOnAction(e -> {
            tabPane.getSelectionModel().select(5); // Vai al tab Raccolti
            //TODO:raccoltoController.nuovoRaccolto();
        });

        Button reportVeloceBtn = creaButtoneAzione("📊 Report Veloce", "#9B59B6");
        reportVeloceBtn.setOnAction(e -> {
            tabPane.getSelectionModel().select(6); // Vai al tab Analisi
            // Il DataProcessingView si occuperà del report
        });

        accessoRapidoBox.getChildren().addAll(
                nuovaZonaBtn, nuovaPiantagioneBtn, nuovoRaccoltoBtn, reportVeloceBtn
        );

        dashboardContent.getChildren().addAll(
                carteStatsBox,
                new Separator(),
                accessoRapidoLabel,
                accessoRapidoBox
        );

        tab.setContent(new ScrollPane(dashboardContent));
        return tab;
    }

    private VBox creaCartaStatistica(String titolo, String valore, String colore) {
        VBox carta = new VBox(10);
        carta.setPadding(new Insets(20));
        carta.setStyle("-fx-background-color: white; -fx-border-color: " + colore +
                "; -fx-border-width: 0 0 3 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        carta.setPrefWidth(200);

        Label titoloLabel = new Label(titolo);
        titoloLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

        Label valoreLabel = new Label(valore);
        valoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + colore + ";");

        carta.getChildren().addAll(titoloLabel, valoreLabel);
        return carta;
    }

    private Button creaButtoneAzione(String testo, String colore) {
        Button btn = new Button(testo);
        btn.setStyle("-fx-background-color: " + colore + "; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-padding: 10 20 10 20; -fx-background-radius: 5;");
        btn.setPrefWidth(150);
        return btn;
    }

    private HBox creaBarraStato() {
        HBox barraStato = new HBox();
        barraStato.setPadding(new Insets(5, 10, 5, 10));
        barraStato.setStyle("-fx-background-color: #ECF0F1; -fx-border-color: #BDC3C7; -fx-border-width: 1 0 0 0;");

        Label statoLabel = new Label("✅ Sistema pronto - Database connesso");
        statoLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dataLabel = new Label("Ultimo aggiornamento: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dataLabel.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 12px;");

        barraStato.getChildren().addAll(statoLabel, spacer, dataLabel);
        return barraStato;
    }

    private Tab creaTabZone() {
        Tab tab = new Tab("🗺️ Zone");
        tab.setContent(zonaView);
        return tab;
    }

    private Tab creaTabFornitori() {
        Tab tab = new Tab("🏢 Fornitori");
        tab.setContent(fornitoreView);
        return tab;
    }

    private Tab creaTabPiante() {
        Tab tab = new Tab("🌿 Piante");
        tab.setContent(piantaView);
        return tab;
    }

    private Tab creaTabPiantagioni() {
        Tab tab = new Tab("🌱 Piantagioni");
        tab.setContent(piantagioneView);
        return tab;
    }

    private Tab creaTabRaccolti() {
        Tab tab = new Tab("🥕 Raccolti");
        tab.setContent(raccoltoView);
        return tab;
    }

    private Tab creaTabElaborazioniDati() {
        Tab tab = new Tab("📈 Analisi & Report");
        tab.setContent(dataProcessingView);
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
