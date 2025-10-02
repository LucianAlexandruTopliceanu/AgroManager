package Main;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Service.*;
import BusinessLogic.Exception.DataAccessException;
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
    private DataProcessingController dataProcessingController;

    // Views
    private ZonaView zonaView;
    private FornitoreView fornitoreView;
    private PiantaView piantaView;
    private PiantagioneView piantagioneView;
    private RaccoltoView raccoltoView;
    private DataProcessingView dataProcessingView;

    @Override
    public void start(Stage primaryStage) {
        // Inizializza il sistema di notifiche PRIMA di tutto
        NotificationHelper.initialize();

        initializeServices();
        initializeViews();
        initializeControllers();
        // Controllers come variabili locali
        primaryStage.setTitle("🌱 AgroManager - Sistema di Gestione Agricola");
        primaryStage.setMaximized(true);

        BorderPane mainLayout = new BorderPane();
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
        VBox menuLaterale = creaMenuLaterale(tabPane);
        mainLayout.setLeft(menuLaterale);
        mainLayout.setCenter(tabPane);
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
        dataProcessingView = new DataProcessingView();
    }

    private void initializeControllers() {
        zonaController = new ZonaController(zonaService, zonaView);
        fornitoreController = new FornitoreController(fornitoreService, fornitoreView);
        piantaController = new PiantaController(piantaService, piantaView);
        piantagioneController = new PiantagioneController(piantagioneService, zonaService, piantaService, piantagioneView);
        raccoltoController = new RaccoltoController(raccoltoService, piantagioneService, raccoltoView);
        dataProcessingController = new DataProcessingController(dataProcessingView, businessLogic);
    }

    private VBox creaMenuLaterale(TabPane tabPane) {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #2C3E50;");

        menu.getChildren().addAll(
            new Label("AgroManager"),
            new Separator(),
            creaMenuButton("Dashboard", e -> tabPane.getSelectionModel().select(0)),
            creaMenuButton("Zone", e -> tabPane.getSelectionModel().select(1)),
            creaMenuButton("Fornitori", e -> tabPane.getSelectionModel().select(2)),
            creaMenuButton("Piante", e -> tabPane.getSelectionModel().select(3)),
            creaMenuButton("Piantagioni", e -> tabPane.getSelectionModel().select(4)),
            creaMenuButton("Raccolti", e -> tabPane.getSelectionModel().select(5)),
            new Separator(),
            creaMenuButton("Analisi", e -> tabPane.getSelectionModel().select(6))
        );

        return menu;
    }

    private Button creaMenuButton(String testo, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(testo);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        btn.setPrefWidth(150);
        btn.setOnAction(action);
        return btn;
    }

    private Tab creaTabDashboard(TabPane tabPane) {
        Tab tab = new Tab("Dashboard");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Statistiche essenziali con gestione errori
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(10);
        statsGrid.setVgap(10);

        try {
            // Prima riga di statistiche
            statsGrid.addRow(0,
                    new Label("Zone:"), new Label(String.valueOf(zonaService.getAllZone().size())),
                    new Label("Piantagioni:"), new Label(String.valueOf(piantagioneService.getAllPiantagioni().size()))
            );

            // Seconda riga di statistiche
            statsGrid.addRow(1,
                    new Label("Raccolti mensili:"), new Label(String.valueOf(raccoltoService.getRaccoltiDelMese().size())),
                    new Label("Produzione totale:"), new Label(String.format("%.2f kg", raccoltoService.getProduzioneTotale()))
            );
        } catch (DataAccessException e) {
            // In caso di errore nel caricamento statistiche, mostra valori di default
            statsGrid.addRow(0,
                    new Label("Zone:"), new Label("Errore"),
                    new Label("Piantagioni:"), new Label("Errore")
            );
            statsGrid.addRow(1,
                    new Label("Raccolti mensili:"), new Label("Errore"),
                    new Label("Produzione totale:"), new Label("Errore")
            );

            // Log dell'errore per debugging ma non interrompe l'avvio dell'app
            System.err.println("Errore caricamento statistiche dashboard: " + e.getMessage());
        } catch (Exception e) {
            // Gestione di qualsiasi altro errore imprevisto
            statsGrid.addRow(0,
                    new Label("Zone:"), new Label("N/A"),
                    new Label("Piantagioni:"), new Label("N/A")
            );
            statsGrid.addRow(1,
                    new Label("Raccolti mensili:"), new Label("N/A"),
                    new Label("Produzione totale:"), new Label("N/A")
            );

            System.err.println("Errore imprevisto nel dashboard: " + e.getMessage());
        }

        // Accesso rapido essenziali
        HBox actionBox = new HBox(10);
        Button nuovaPiantagioneBtn = new Button("Nuova Piantagione");
        nuovaPiantagioneBtn.setOnAction(e -> {
            tabPane.getSelectionModel().select(4);
            piantagioneController.onNuovaPiantagione();
        });

        Button nuovoRaccoltoBtn = new Button("Nuovo Raccolto");
        nuovoRaccoltoBtn.setOnAction(e -> {
            tabPane.getSelectionModel().select(5);
            raccoltoController.onNuovoRaccolto();
        });

        actionBox.getChildren().addAll(nuovaPiantagioneBtn, nuovoRaccoltoBtn);

        content.getChildren().addAll(
                new Label("Statistiche"),
                statsGrid,
                new Separator(),
                new Label("Azioni rapide"),
                actionBox
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        tab.setContent(scroll);
        return tab;
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
