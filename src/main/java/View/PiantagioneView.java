package View;

import DomainModel.Piantagione;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class PiantagioneView extends VBox {

    private final TableView<Piantagione> tablePiantagioni;
    private final ObservableList<Piantagione> piantagioniData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button aggiornaBtn;
    private final Button visualizzaDettagliBtn;

    // Controlli di ricerca e filtro
    private final ComboBox<String> filtroPiantaCombo;
    private final ComboBox<String> filtroZonaCombo;
    private final DatePicker filtroDataDa;
    private final DatePicker filtroDataA;

    public PiantagioneView() {
        tablePiantagioni = new TableView<>();
        piantagioniData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuova Piantagione");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        aggiornaBtn = new Button("🔄 Aggiorna");
        visualizzaDettagliBtn = new Button("👁️ Dettagli");
        filtroPiantaCombo = new ComboBox<>();
        filtroZonaCombo = new ComboBox<>();
        filtroDataDa = new DatePicker();
        filtroDataA = new DatePicker();

        setupLayout();
        setupTable();
        setupControls();
    }

    private void setupLayout() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #F8F9FA;");
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        TableColumn<Piantagione, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Piantagione, String> piantaCol = new TableColumn<>("Pianta ID");
        piantaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getPiantaId() != null ?
                cell.getValue().getPiantaId().toString() : "N/A"));
        piantaCol.setPrefWidth(100);

        TableColumn<Piantagione, String> zonaCol = new TableColumn<>("Zona ID");
        zonaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getZonaId() != null ?
                cell.getValue().getZonaId().toString() : "N/A"));
        zonaCol.setPrefWidth(100);

        TableColumn<Piantagione, String> quantitaCol = new TableColumn<>("Quantità Piante");
        quantitaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getQuantitaPianta() != null ?
                cell.getValue().getQuantitaPianta().toString() : "N/A"));
        quantitaCol.setPrefWidth(120);

        TableColumn<Piantagione, String> dataCol = new TableColumn<>("Messa a Dimora");
        dataCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getMessaADimora() != null ?
                cell.getValue().getMessaADimora().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        dataCol.setPrefWidth(120);

        TableColumn<Piantagione, String> creazioneCol = new TableColumn<>("Data Creazione");
        creazioneCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getDataCreazione() != null ?
                cell.getValue().getDataCreazione().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"));
        creazioneCol.setPrefWidth(150);

        tablePiantagioni.getColumns().addAll(idCol, piantaCol, zonaCol, quantitaCol, dataCol, creazioneCol);
        tablePiantagioni.setItems(piantagioniData);

        // Double-click per dettagli
        tablePiantagioni.setRowFactory(tv -> {
            TableRow<Piantagione> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    visualizzaDettagli();
                }
            });
            return row;
        });

        VBox.setVgrow(tablePiantagioni, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione filtri avanzati
        VBox filtriBox = new VBox(10);
        filtriBox.setPadding(new Insets(15));
        filtriBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label filtriLabel = new Label("🔍 Filtri Avanzati");
        filtriLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane filtriGrid = new GridPane();
        filtriGrid.setHgap(10);
        filtriGrid.setVgap(10);

        filtroPiantaCombo.setPromptText("Filtra per tipo pianta");
        filtroPiantaCombo.setPrefWidth(180);
        filtroPiantaCombo.setOnAction(e -> filtraPiantagioni());

        filtroZonaCombo.setPromptText("Filtra per zona");
        filtroZonaCombo.setPrefWidth(180);
        filtroZonaCombo.setOnAction(e -> filtraPiantagioni());

        filtroDataDa.setPromptText("Da data");
        filtroDataDa.setPrefWidth(130);
        filtroDataDa.setOnAction(e -> filtraPiantagioni());

        filtroDataA.setPromptText("A data");
        filtroDataA.setPrefWidth(130);
        filtroDataA.setOnAction(e -> filtraPiantagioni());

        filtriGrid.add(new Label("Pianta:"), 0, 0);
        filtriGrid.add(filtroPiantaCombo, 1, 0);
        filtriGrid.add(new Label("Zona:"), 2, 0);
        filtriGrid.add(filtroZonaCombo, 3, 0);
        filtriGrid.add(new Label("Periodo:"), 0, 1);
        filtriGrid.add(filtroDataDa, 1, 1);
        filtriGrid.add(new Label("→"), 2, 1);
        filtriGrid.add(filtroDataA, 3, 1);

        filtriBox.getChildren().addAll(filtriLabel, filtriGrid);

        // Sezione azioni
        VBox azioniBox = new VBox(10);
        azioniBox.setPadding(new Insets(15));
        azioniBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label azioniLabel = new Label("⚡ Azioni");
        azioniLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox pulsantiBox = new HBox(10);

        // Stili pulsanti
        nuovoBtn.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; " +
                         "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");

        modificaBtn.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        modificaBtn.setDisable(true);

        eliminaBtn.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        eliminaBtn.setDisable(true);

        aggiornaBtn.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");

        visualizzaDettagliBtn.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white; " +
                                      "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        visualizzaDettagliBtn.setDisable(true);

        // Gestione selezione
        tablePiantagioni.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
            visualizzaDettagliBtn.setDisable(!hasSelection);
        });

        pulsantiBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn, visualizzaDettagliBtn, aggiornaBtn);
        azioniBox.getChildren().addAll(azioniLabel, pulsantiBox);

        getChildren().addAll(filtriBox, azioniBox, tablePiantagioni);
    }

    private void filtraPiantagioni() {
        // Il controller implementerà la logica di filtro
    }

    private void visualizzaDettagli() {
        if (getPiantagioneSelezionata() != null) {
            // Il controller gestirà la visualizzazione dei dettagli
        }
    }

    // Metodi pubblici per il controller
    public void setPiantagioni(java.util.List<Piantagione> piantagioni) {
        piantagioniData.setAll(piantagioni);
    }

    public Piantagione getPiantagioneSelezionata() {
        return tablePiantagioni.getSelectionModel().getSelectedItem();
    }

    public void setOnNuovaPiantagione(Consumer<Void> handler) {
        nuovoBtn.setOnAction(e -> handler.accept(null));
    }

    public void setOnModificaPiantagione(Consumer<Void> handler) {
        modificaBtn.setOnAction(e -> handler.accept(null));
    }

    public void setOnEliminaPiantagione(Consumer<Void> handler) {
        eliminaBtn.setOnAction(e -> handler.accept(null));
    }

    public void setOnAggiornaPiantagioni(Consumer<Void> handler) {
        aggiornaBtn.setOnAction(e -> handler.accept(null));
    }

    public void setOnVisualizzaDettagli(Consumer<Void> handler) {
        visualizzaDettagliBtn.setOnAction(e -> handler.accept(null));
    }

    public void setPiante(java.util.List<String> piante) {
        filtroPiantaCombo.getItems().clear();
        filtroPiantaCombo.getItems().add("Tutte");
        filtroPiantaCombo.getItems().addAll(piante);
        filtroPiantaCombo.setValue("Tutte");
    }

    public void setZone(java.util.List<String> zone) {
        filtroZonaCombo.getItems().clear();
        filtroZonaCombo.getItems().add("Tutte");
        filtroZonaCombo.getItems().addAll(zone);
        filtroZonaCombo.setValue("Tutte");
    }
}
