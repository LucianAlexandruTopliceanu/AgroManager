@Override
public void start(Stage primaryStage) {
    primaryStage.setTitle(" AgroManager - Sistema di Gestione Agricola");
    primaryStage.setMaximized(true);
    NotificationHelper.initialize();
    showLoadingScreen(primaryStage);
    initializeApplicationAsync(primaryStage);
}
private void initializeApplicationAsync(StageprimaryStage) {
    Task<Void> initTask = new Task<>() {
        @Override
        protected Void call() throws Exception {
            Platform.runLater(() -> statusLabel.setText("Inizializzazione servizi..."));
            try {
                initializeServices();
                systemReady = true;
                Platform.runLater(() -> statusLabel.setText("Sistema pronto "));
            } catch (Exception e) {...}
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
    
    // Altre parti del codice...
}

private void initializeServices() {
    fornitoreService = new FornitoreService(DAOFactory.getFornitoreDAO());
    // Altre inizializzazioni dei services
}
private void initializeViews() {
    fornitoreView = new FornitoreView();
    // Altre inizializzazioni delle views
}
private void initializeControllers() {
    fornitoreController = new FornitoreController(fornitoreService, fornitoreView);
    // Altre inizializzazioni dei controllers
}