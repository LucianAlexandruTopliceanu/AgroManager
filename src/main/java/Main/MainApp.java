package Main;

import BusinessLogic.BusinessLogic;
import BusinessLogic.Service.*;
import BusinessLogic.Exception.DataAccessException;
import Controller.*;
import ORM.DAOFactory;
import View.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;

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

    // Status components
    private Label statusLabel;
    private ProgressIndicator loadingIndicator;
    private BorderPane mainLayout;
    private boolean systemReady = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🌱 AgroManager - Sistema di Gestione Agricola");
        primaryStage.setMaximized(true);

        // Inizializza il sistema di notifiche PRIMA di tutto
        NotificationHelper.initialize();

        // Mostra schermata di caricamento
        showLoadingScreen(primaryStage);

        // Inizializza l'applicazione in background
        initializeApplicationAsync(primaryStage);
    }

    private void showLoadingScreen(Stage primaryStage) {
        VBox loadingBox = new VBox(20);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setStyle("-fx-background-color: #f8f9fa;");

        Label titleLabel = new Label("🌱 AgroManager");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(60, 60);

        statusLabel = new Label("Inizializzazione in corso...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        loadingBox.getChildren().addAll(titleLabel, loadingIndicator, statusLabel);

        Scene loadingScene = new Scene(loadingBox, 400, 300);
        primaryStage.setScene(loadingScene);
        primaryStage.show();
    }

    private void initializeApplicationAsync(Stage primaryStage) {
        Task<Void> initTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> statusLabel.setText("Inizializzazione servizi..."));

                // Tenta di inizializzare i servizi - la gestione errori è delegata ai servizi stessi
                try {
                    initializeServices();
                    systemReady = true;
                    Platform.runLater(() -> statusLabel.setText("Sistema pronto ✓"));
                } catch (Exception e) {
                    systemReady = false;
                    Platform.runLater(() -> statusLabel.setText("⚠️ Modalità limitata"));
                    // L'ErrorService gestirà la notifica dell'errore
                    ErrorService.handleException("Inizializzazione servizi", e);
                    Thread.sleep(1000); // Breve pausa per far vedere il messaggio
                }

                Platform.runLater(() -> statusLabel.setText("Preparazione interfaccia..."));
                initializeViews();
                initializeControllers();

                Thread.sleep(500); // Breve pausa per un caricamento fluido
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> showMainInterface(primaryStage));
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> showErrorScreen(primaryStage, getException()));
            }
        };

        new Thread(initTask).start();
    }

    private void showMainInterface(Stage primaryStage) {
        mainLayout = new BorderPane();
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-tab-min-height: 35px; -fx-tab-max-height: 35px;");

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

        // Carica gli stili CSS se disponibili
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        } catch (Exception e) {
            // CSS file non trovato, usa stili di default
            applyDefaultStyles(scene);
        }

        primaryStage.setScene(scene);
    }

    private void showErrorScreen(Stage primaryStage, Throwable error) {
        VBox errorBox = new VBox(20);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setStyle("-fx-background-color: #fff5f5; -fx-padding: 40px;");

        Label errorTitle = new Label("⚠️ Errore di Inizializzazione");
        errorTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        TextArea errorDetails = new TextArea(
                "Si è verificato un errore critico durante l'avvio dell'applicazione:\n\n" +
                error.getMessage() + "\n\n" +
                "L'applicazione non può continuare."
        );
        errorDetails.setEditable(false);
        errorDetails.setPrefRowCount(6);
        errorDetails.setMaxWidth(600);

        Button retryButton = new Button("🔄 Riprova");
        retryButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px;");
        retryButton.setOnAction(e -> {
            showLoadingScreen(primaryStage);
            initializeApplicationAsync(primaryStage);
        });

        Button exitButton = new Button("❌ Esci");
        exitButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px 20px;");
        exitButton.setOnAction(e -> Platform.exit());

        HBox buttonBox = new HBox(15, retryButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);

        errorBox.getChildren().addAll(errorTitle, errorDetails, buttonBox);

        Scene errorScene = new Scene(new ScrollPane(errorBox), 800, 600);
        primaryStage.setScene(errorScene);
    }

    private void applyDefaultStyles(Scene scene) {
        // Applica stili di default se il CSS non è disponibile
        scene.getRoot().setStyle("-fx-font-family: 'System'; -fx-font-size: 13px;");
    }

    private void initializeServices() {
        // Inizializzazione tramite i servizi - ogni servizio gestirà internamente i propri errori
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

    // Metodo per verificare se il sistema è operativo - delega ai servizi la verifica
    private boolean isSystemOperational() {
        if (!systemReady) return false;

        try {
            // Test semplice attraverso un servizio - non accesso diretto al database
            zonaService.getAllZone();
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private VBox creaMenuLaterale(TabPane tabPane) {
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(15));
        menu.setStyle("-fx-background-color: #2c3e50; -fx-min-width: 200px; -fx-max-width: 200px;");

        Label titleLabel = new Label("AgroManager");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-padding: 0 0 10 0;");

        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background: #34495e;");

        boolean systemOperational = isSystemOperational();

        VBox menuItems = new VBox(3);
        menuItems.getChildren().addAll(
            creaMenuButton("📊 Dashboard", e -> tabPane.getSelectionModel().select(0), true),
            creaMenuButton("🗺️ Zone", e -> tabPane.getSelectionModel().select(1), systemOperational),
            creaMenuButton("🏢 Fornitori", e -> tabPane.getSelectionModel().select(2), systemOperational),
            creaMenuButton("🌿 Piante", e -> tabPane.getSelectionModel().select(3), systemOperational),
            creaMenuButton("🌱 Piantagioni", e -> tabPane.getSelectionModel().select(4), systemOperational),
            creaMenuButton("🥕 Raccolti", e -> tabPane.getSelectionModel().select(5), systemOperational)
        );

        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background: #34495e;");

        Button analisiBtn = creaMenuButton("📈 Analisi & Report", e -> tabPane.getSelectionModel().select(6), systemOperational);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Status indicator nel menu
        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusIcon = new Label(systemOperational ? "🟢" : "🔴");
        Label statusText = new Label(systemOperational ? "Operativo" : "Limitato");
        statusText.setStyle("-fx-text-fill: " + (systemOperational ? "#27ae60" : "#e74c3c") + "; -fx-font-size: 11px;");
        statusBox.getChildren().addAll(statusIcon, statusText);

        menu.getChildren().addAll(
            titleLabel,
            separator1,
            menuItems,
            separator2,
            analisiBtn,
            spacer,
            statusBox
        );

        return menu;
    }

    private Button creaMenuButton(String testo, javafx.event.EventHandler<javafx.event.ActionEvent> action, boolean enabled) {
        Button btn = new Button(testo);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefWidth(170);
        btn.setPrefHeight(35);

        if (enabled) {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                        "-fx-border-color: transparent; -fx-padding: 8px 12px; " +
                        "-fx-cursor: hand; -fx-font-size: 13px;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1; " +
                                                   "-fx-border-color: transparent; -fx-padding: 8px 12px; " +
                                                   "-fx-cursor: hand; -fx-font-size: 13px;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                                                  "-fx-border-color: transparent; -fx-padding: 8px 12px; " +
                                                  "-fx-cursor: hand; -fx-font-size: 13px;"));
            btn.setOnAction(action);
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; " +
                        "-fx-border-color: transparent; -fx-padding: 8px 12px; " +
                        "-fx-opacity: 0.6; -fx-font-size: 13px;");
            btn.setDisable(true);
        }

        return btn;
    }

    private Tab creaTabDashboard(TabPane tabPane) {
        Tab tab = new Tab("📊 Dashboard");
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f8f9fa;");

        // Header del dashboard
        Label headerLabel = new Label("🌱 Sistema di Gestione Agricola");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Card container per le statistiche
        HBox statsContainer = new HBox(15);
        statsContainer.setAlignment(Pos.CENTER);

        if (systemReady) {
            try {
                // Card Zone
                VBox zoneCard = createStatsCard("🗺️", "Zone", String.valueOf(zonaService.getAllZone().size()), "#3498db");

                // Card Fornitori
                VBox fornitoriCard = createStatsCard("🏢", "Fornitori", String.valueOf(fornitoreService.getAllFornitori().size()), "#e67e22");

                // Card Piante
                VBox pianteCard = createStatsCard("🌿", "Piante", String.valueOf(piantaService.getAllPiante().size()), "#27ae60");

                // Card Piantagioni
                VBox piantagioniCard = createStatsCard("🌱", "Piantagioni", String.valueOf(piantagioneService.getAllPiantagioni().size()), "#9b59b6");

                statsContainer.getChildren().addAll(zoneCard, fornitoriCard, pianteCard, piantagioniCard);

            } catch (DataAccessException e) {
                // Mostra card di errore
                VBox errorCard = createErrorCard("Errore nel caricamento dei dati", "Verificare la connessione al database");
                statsContainer.getChildren().add(errorCard);
            }
        } else {
            // Mostra stato offline
            VBox offlineCard = createErrorCard("Database Offline", "L'applicazione è in modalità limitata");
            statsContainer.getChildren().add(offlineCard);
        }

        // Sezione azioni rapide
        Label actionsHeader = new Label("🚀 Azioni Rapide");
        actionsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        FlowPane actionsPane = new FlowPane(15, 15);
        actionsPane.setAlignment(Pos.CENTER);

        if (systemReady) {
            Button nuovaZonaBtn = createActionButton("➕ Nuova Zona", "Aggiungi una nuova zona di coltivazione", e -> {
                tabPane.getSelectionModel().select(1);
                zonaController.onNuovaZona();
            });

            Button nuovoFornitoreBtn = createActionButton("➕ Nuovo Fornitore", "Registra un nuovo fornitore", e -> {
                tabPane.getSelectionModel().select(2);
                fornitoreController.onNuovoFornitore();
            });

            Button nuovaPiantaBtn = createActionButton("➕ Nuova Pianta", "Aggiungi una nuova varietà di pianta", e -> {
                tabPane.getSelectionModel().select(3);
                piantaController.onNuovaPianta();
            });

            Button nuovaPiantagioneBtn = createActionButton("🌱 Nuova Piantagione", "Crea una nuova piantagione", e -> {
                tabPane.getSelectionModel().select(4);
                piantagioneController.onNuovaPiantagione();
            });

            Button nuovoRaccoltoBtn = createActionButton("🥕 Nuovo Raccolto", "Registra un nuovo raccolto", e -> {
                tabPane.getSelectionModel().select(5);
                raccoltoController.onNuovoRaccolto();
            });

            Button analisiBtn = createActionButton("📈 Analisi Dati", "Visualizza report e statistiche", e -> {
                tabPane.getSelectionModel().select(6);
            });

            actionsPane.getChildren().addAll(nuovaZonaBtn, nuovoFornitoreBtn, nuovaPiantaBtn,
                                           nuovaPiantagioneBtn, nuovoRaccoltoBtn, analisiBtn);
        } else {
            Label offlineMessage = new Label("🔌 Connetti il database per accedere alle funzionalità");
            offlineMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-padding: 20px;");
            actionsPane.getChildren().add(offlineMessage);
        }

        content.getChildren().addAll(headerLabel, statsContainer, actionsHeader, actionsPane);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f8f9fa;");
        tab.setContent(scroll);
        return tab;
    }

    private VBox createStatsCard(String icon, String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(180, 120);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setPadding(new Insets(15));

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    private VBox createErrorCard(String title, String message) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(400, 120);
        card.setStyle("-fx-background-color: #fff5f5; -fx-background-radius: 8px; -fx-border-color: #e74c3c; -fx-border-width: 1px; -fx-border-radius: 8px;");
        card.setPadding(new Insets(15));

        Label iconLabel = new Label("⚠️");
        iconLabel.setStyle("-fx-font-size: 32px;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #c0392b;");

        card.getChildren().addAll(iconLabel, titleLabel, messageLabel);
        return card;
    }

    private Button createActionButton(String title, String description, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox buttonContent = new VBox(5);
        buttonContent.setAlignment(Pos.CENTER);
        buttonContent.setPrefSize(200, 80);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #ecf0f1; -fx-wrap-text: true;");
        descLabel.setMaxWidth(180);

        buttonContent.getChildren().addAll(titleLabel, descLabel);

        Button button = new Button();
        button.setGraphic(buttonContent);
        button.setPrefSize(200, 80);
        button.setStyle("-fx-background-color: #3498db; -fx-background-radius: 8px; -fx-cursor: hand;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-background-radius: 8px; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-background-radius: 8px; -fx-cursor: hand;"));

        button.setOnAction(action);
        return button;
    }

    private HBox creaBarraStato() {
        HBox barraStato = new HBox();
        barraStato.setPadding(new Insets(8, 15, 8, 15));
        barraStato.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");

        // Status del database
        HBox dbStatus = new HBox(8);
        dbStatus.setAlignment(Pos.CENTER_LEFT);

        Label dbIcon = new Label(systemReady ? "🟢" : "🔴");
        Label dbLabel = new Label(systemReady ? "Database connesso" : "Database offline");
        dbLabel.setStyle("-fx-text-fill: " + (systemReady ? "#27ae60" : "#e74c3c") + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        dbStatus.getChildren().addAll(dbIcon, dbLabel);

        // Spacer centrale
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Informazioni di sistema
        VBox systemInfo = new VBox(2);
        systemInfo.setAlignment(Pos.CENTER_RIGHT);

        Label userLabel = new Label("👤 Utente: Sistema");
        userLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        Label timeLabel = new Label("🕒 " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        timeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        systemInfo.getChildren().addAll(userLabel, timeLabel);

        barraStato.getChildren().addAll(dbStatus, spacer, systemInfo);
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
