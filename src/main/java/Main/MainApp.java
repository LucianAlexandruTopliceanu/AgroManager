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
    private StatoPiantagioneService statoPiantagioneService;
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
    private boolean systemReady = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🌱 AgroManager - Sistema di Gestione Agricola");
        primaryStage.setMaximized(true);

        NotificationHelper.initialize();
        showLoadingScreen(primaryStage);
        initializeApplicationAsync(primaryStage);
    }

    private void showLoadingScreen(Stage primaryStage) {
        VBox loadingBox = new VBox(20);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setStyle("-fx-background-color: #f8f9fa;");

        Label titleLabel = new Label("🌱 AgroManager");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(60, 60);

        statusLabel = new Label("Inizializzazione in corso...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        loadingBox.getChildren().addAll(titleLabel, loadingIndicator, statusLabel);

        Scene loadingScene = new Scene(loadingBox, 400, 300);
        primaryStage.setScene(loadingScene);
        primaryStage.show();
    }

    private void initializeApplicationAsync(Stage primaryStage) {
        Task<Void> initTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> statusLabel.setText("Inizializzazione servizi..."));

                try {
                    initializeServices();
                    systemReady = true;
                    Platform.runLater(() -> statusLabel.setText("Sistema pronto ✓"));
                } catch (Exception e) {
                    systemReady = false;
                    Platform.runLater(() -> statusLabel.setText("⚠️ Modalità limitata"));
                    ErrorService.handleException("Inizializzazione servizi", e);
                    Thread.sleep(1000);
                }

                Platform.runLater(() -> statusLabel.setText("Preparazione interfaccia..."));
                initializeViews();
                initializeControllers();

                Thread.sleep(500);
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
        BorderPane mainLayout = new BorderPane();
        StackPane centerPane = new StackPane();
        centerPane.setStyle("-fx-background-color: #f8f9fa;");

        VBox menuLaterale = creaMenuLaterale(centerPane);
        mainLayout.setLeft(menuLaterale);
        mainLayout.setCenter(centerPane);

        HBox barraStato = creaBarraStato();
        mainLayout.setBottom(barraStato);

        centerPane.getChildren().add(creaDashboard(centerPane));

        Scene scene = new Scene(mainLayout, 1400, 900);

        try {
            var cssResource = getClass().getResource("/styles/application.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
        } catch (Exception e) {
            // CSS non disponibile
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
                error.getMessage() + "\n\nL'applicazione non può continuare."
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

    private void initializeServices() {
        DAOFactory daoFactory = DAOFactory.getInstance();

        zonaService = new ZonaService(daoFactory.getZonaDAO());
        fornitoreService = new FornitoreService(daoFactory.getFornitoreDAO());
        piantaService = new PiantaService(daoFactory.getPiantaDAO());
        piantagioneService = new PiantagioneService(daoFactory.getPiantagioneDAO());
        raccoltoService = new RaccoltoService(daoFactory.getRaccoltoDAO());
        statoPiantagioneService = new StatoPiantagioneService(daoFactory.getStatoPiantagioneDAO());
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
        piantaController = new PiantaController(piantaService, fornitoreService, piantaView);
        piantagioneController = new PiantagioneController(piantagioneService, zonaService, piantaService, statoPiantagioneService, piantagioneView);
        raccoltoController = new RaccoltoController(raccoltoService, piantagioneService, raccoltoView);

        try {
            dataProcessingController = new DataProcessingController(dataProcessingView, businessLogic);
        } catch (Exception e) {
            ErrorService.handleException("Inizializzazione DataProcessingController", e);
        }
    }

    private boolean isSystemOperational() {
        if (!systemReady) return false;

        try {
            zonaService.getAllZone();
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private VBox creaMenuLaterale(StackPane centerPane) {
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
            creaMenuButton("📊 Dashboard", e -> mostraView(centerPane, creaDashboard(centerPane)), true),
            creaMenuButton("🗺️ Zone", e -> mostraView(centerPane, zonaView), systemOperational),
            creaMenuButton("🏢 Fornitori", e -> mostraView(centerPane, fornitoreView), systemOperational),
            creaMenuButton("🌿 Piante", e -> mostraView(centerPane, piantaView), systemOperational),
            creaMenuButton("🌱 Piantagioni", e -> mostraView(centerPane, piantagioneView), systemOperational),
            creaMenuButton("🥕 Raccolti", e -> mostraView(centerPane, raccoltoView), systemOperational)
        );

        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background: #34495e;");

        Button analisiBtn = creaMenuButton("📈 Analisi & Report", e -> mostraView(centerPane, dataProcessingView), systemOperational);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusIcon = new Label(systemOperational ? "🟢" : "🔴");
        Label statusText = new Label(systemOperational ? "Operativo" : "Offline");
        statusText.setStyle("-fx-text-fill: " + (systemOperational ? "#27ae60" : "#e74c3c") + "; -fx-font-size: 11px;");
        statusBox.getChildren().addAll(statusIcon, statusText);

        menu.getChildren().addAll(titleLabel, separator1, menuItems, separator2, analisiBtn, spacer, statusBox);
        return menu;
    }

    private void mostraView(StackPane centerPane, javafx.scene.Node view) {
        centerPane.getChildren().clear();
        centerPane.getChildren().add(view);
    }

    private Button creaMenuButton(String testo, javafx.event.EventHandler<javafx.event.ActionEvent> action, boolean enabled) {
        Button btn = new Button(testo);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefWidth(170);
        btn.setPrefHeight(35);

        if (enabled) {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-border-color: transparent; -fx-padding: 8px 12px; -fx-cursor: hand; -fx-font-size: 13px;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ecf0f1; -fx-border-color: transparent; -fx-padding: 8px 12px; -fx-cursor: hand; -fx-font-size: 13px;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-border-color: transparent; -fx-padding: 8px 12px; -fx-cursor: hand; -fx-font-size: 13px;"));
            btn.setOnAction(action);
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; -fx-border-color: transparent; -fx-padding: 8px 12px; -fx-opacity: 0.6; -fx-font-size: 13px;");
            btn.setDisable(true);
        }

        return btn;
    }

    private ScrollPane creaDashboard(StackPane centerPane) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #f8f9fa;");

        Label headerLabel = new Label("🌱 Sistema di Gestione Agricola");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox statsContainer = new HBox(15);
        statsContainer.setAlignment(Pos.CENTER);

        if (systemReady) {
            try {
                statsContainer.getChildren().addAll(
                    createStatsCard("🗺️", "Zone", String.valueOf(zonaService.getAllZone().size()), "#3498db"),
                    createStatsCard("🏢", "Fornitori", String.valueOf(fornitoreService.getAllFornitori().size()), "#e67e22"),
                    createStatsCard("🌿", "Piante", String.valueOf(piantaService.getAllPiante().size()), "#27ae60"),
                    createStatsCard("🌱", "Piantagioni", String.valueOf(piantagioneService.getAllPiantagioni().size()), "#9b59b6"),
                    createStatsCard("🥕", "Raccolti", String.valueOf(raccoltoService.getAllRaccolti().size()), "#e74c3c")
                );
            } catch (DataAccessException e) {
                statsContainer.getChildren().add(createErrorCard("Errore nel caricamento dei dati", "Verificare la connessione al database"));
            }
        } else {
            statsContainer.getChildren().add(createErrorCard("Database Offline", "L'applicazione è in modalità limitata"));
        }

        Label actionsHeader = new Label("🚀 Azioni Rapide");
        actionsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        FlowPane actionsPane = new FlowPane(15, 15);
        actionsPane.setAlignment(Pos.CENTER);

        if (systemReady) {
            actionsPane.getChildren().addAll(
                createActionButton("➕ Nuova Zona", "Aggiungi una nuova zona di coltivazione", e -> {
                    mostraView(centerPane, zonaView);
                    if (zonaController != null) zonaController.onNuovaZona();
                }),
                createActionButton("➕ Nuovo Fornitore", "Registra un nuovo fornitore", e -> {
                    mostraView(centerPane, fornitoreView);
                    if (fornitoreController != null) fornitoreController.onNuovoFornitore();
                }),
                createActionButton("➕ Nuova Pianta", "Aggiungi una nuova varietà di pianta", e -> {
                    mostraView(centerPane, piantaView);
                    if (piantaController != null) piantaController.onNuovaPianta();
                }),
                createActionButton("🌱 Nuova Piantagione", "Crea una nuova piantagione", e -> {
                    mostraView(centerPane, piantagioneView);
                    if (piantagioneController != null) piantagioneController.onNuovaPiantagione();
                }),
                createActionButton("🥕 Nuovo Raccolto", "Registra un nuovo raccolto", e -> {
                    mostraView(centerPane, raccoltoView);
                    if (raccoltoController != null) raccoltoController.onNuovoRaccolto();
                }),
                createActionButton("📈 Analisi Dati", "Visualizza report e statistiche", e -> mostraView(centerPane, dataProcessingView))
            );
        } else {
            Label offlineMessage = new Label("🔌 Connetti il database per accedere alle funzionalità");
            offlineMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-padding: 20px;");
            actionsPane.getChildren().add(offlineMessage);
        }

        content.getChildren().addAll(headerLabel, statsContainer, actionsHeader, actionsPane);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #f8f9fa;");
        return scroll;
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

        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Label statusIcon = new Label(systemReady ? "🟢" : "🔴");
        Label statusLabel = new Label(systemReady ? "Sistema Operativo" : "Sistema Offline");
        statusLabel.setStyle("-fx-text-fill: " + (systemReady ? "#27ae60" : "#e74c3c") + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        statusBox.getChildren().addAll(statusIcon, statusLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label("🕒 " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        timeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        barraStato.getChildren().addAll(statusBox, spacer, timeLabel);
        return barraStato;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

