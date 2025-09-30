package View;

import DomainModel.Zona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class ZonaView extends VBox {

    private final TableView<Zona> tableZone;
    private final ObservableList<Zona> zoneData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button aggiornaBtn;

    // Controlli di ricerca e filtro
    private final TextField ricercaField;
    private final ComboBox<String> filtroTipoTerreno;

    public ZonaView() {
        // Inizializzazione
        tableZone = new TableView<>();
        zoneData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuova Zona");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        aggiornaBtn = new Button("🔄 Aggiorna");
        ricercaField = new TextField();
        filtroTipoTerreno = new ComboBox<>();

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
        // Configurazione colonne
        TableColumn<Zona, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(80);

        TableColumn<Zona, String> nomeCol = new TableColumn<>("Nome Zona");
        nomeCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        nomeCol.setPrefWidth(200);

        TableColumn<Zona, String> dimCol = new TableColumn<>("Dimensione (ha)");
        dimCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getDimensione() != null ?
                String.format("%.2f", cell.getValue().getDimensione()) : "N/A"));
        dimCol.setPrefWidth(120);

        TableColumn<Zona, String> tipoCol = new TableColumn<>("Tipo Terreno");
        tipoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipoTerreno()));
        tipoCol.setPrefWidth(150);

        TableColumn<Zona, String> dataCol = new TableColumn<>("Data Creazione");
        dataCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getDataCreazione() != null ?
                cell.getValue().getDataCreazione().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        dataCol.setPrefWidth(120);

        tableZone.getColumns().addAll(idCol, nomeCol, dimCol, tipoCol, dataCol);
        tableZone.setItems(zoneData);
        tableZone.setRowFactory(tv -> {
            TableRow<Zona> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaZona();
                }
            });
            return row;
        });

        VBox.setVgrow(tableZone, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione ricerca e filtri
        VBox ricercaBox = new VBox(10);
        ricercaBox.setPadding(new Insets(15));
        ricercaBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label ricercaLabel = new Label("🔍 Ricerca e Filtri");
        ricercaLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox ricercaControls = new HBox(10);
        ricercaField.setPromptText("Cerca per nome zona...");
        ricercaField.setPrefWidth(200);
        ricercaField.textProperty().addListener((obs, oldVal, newVal) -> filtraZone());

        filtroTipoTerreno.setPromptText("Filtra per tipo terreno");
        filtroTipoTerreno.getItems().addAll("Tutti", "Argilloso", "Sabbioso", "Limoso", "Misto");
        filtroTipoTerreno.setValue("Tutti");
        filtroTipoTerreno.setOnAction(e -> filtraZone());

        ricercaControls.getChildren().addAll(
            new Label("Nome:"), ricercaField,
            new Label("Tipo:"), filtroTipoTerreno
        );

        ricercaBox.getChildren().addAll(ricercaLabel, ricercaControls);

        // Sezione pulsanti azione
        VBox azioniBox = new VBox(10);
        azioniBox.setPadding(new Insets(15));
        azioniBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label azioniLabel = new Label("⚡ Azioni Rapide");
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

        // Abilita/disabilita pulsanti in base alla selezione
        tableZone.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });

        pulsantiBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn, aggiornaBtn);
        azioniBox.getChildren().addAll(azioniLabel, pulsantiBox);

        getChildren().addAll(ricercaBox, azioniBox, tableZone);
    }

    private void filtraZone() {
        // Implementa logica di filtro
        String testoCerca = ricercaField.getText().toLowerCase();
        String tipoFiltro = filtroTipoTerreno.getValue();

        // Per ora mostra tutti i dati - il controller implementerà il filtro reale
    }

    private void modificaZona() {
        if (getZonaSelezionata() != null) {
            // Il controller gestirà l'apertura del dialog di modifica
        }
    }

    // Metodi pubblici per il controller
    public void setZone(java.util.List<Zona> zone) {
        zoneData.setAll(zone);
    }

    public Zona getZonaSelezionata() {
        return tableZone.getSelectionModel().getSelectedItem();
    }

    public void setOnNuovaZona(Consumer<Void> handler) {
        nuovoBtn.setOnAction(e -> handler.accept(null));
    }

    public void setOnModificaZona(Consumer<Void> handler) {
        modificaBtn.setOnAction(e -> handler.accept(null));
    }

    public void setOnEliminaZona(Consumer<Void> handler) {
        eliminaBtn.setOnAction(e -> handler.accept(null));
    }

    public void setOnAggiornaZone(Consumer<Void> handler) {
        aggiornaBtn.setOnAction(e -> handler.accept(null));
    }

    public String getTestoRicerca() {
        return ricercaField.getText();
    }

    public String getFiltroTipoTerreno() {
        return filtroTipoTerreno.getValue();
    }
}
