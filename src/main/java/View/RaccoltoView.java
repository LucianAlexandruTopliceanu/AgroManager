package View;

import DomainModel.Raccolto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class RaccoltoView extends VBox {

    private final TableView<Raccolto> tableRaccolti;
    private final ObservableList<Raccolto> raccoltiData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button aggiornaBtn;

    // Controlli di ricerca e filtro
    private final ComboBox<String> filtroPiantagioneCombo;
    private final DatePicker filtroDataDa;
    private final DatePicker filtroDataA;
    private final Spinner<Double> filtroQuantitaMin;
    private final Spinner<Double> filtroQuantitaMax;

    // Callback per notificare il controller sui cambiamenti dei filtri
    private java.util.function.Consumer<String> onFiltroPiantagioneChanged;
    private java.util.function.Consumer<java.time.LocalDate> onFiltroDataDaChanged;
    private java.util.function.Consumer<java.time.LocalDate> onFiltroDataAChanged;
    private java.util.function.Consumer<Double> onFiltroQuantitaMinChanged;
    private java.util.function.Consumer<Double> onFiltroQuantitaMaxChanged;

    public RaccoltoView() {
        tableRaccolti = new TableView<>();
        raccoltiData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuovo Raccolto");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        aggiornaBtn = new Button("🔄 Aggiorna");
        filtroPiantagioneCombo = new ComboBox<>();
        filtroDataDa = new DatePicker();
        filtroDataA = new DatePicker();
        filtroQuantitaMin = new Spinner<>(0.0, 1000.0, 0.0, 0.1);
        filtroQuantitaMax = new Spinner<>(0.0, 1000.0, 1000.0, 0.1);

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
        TableColumn<Raccolto, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Raccolto, String> piantagioneCol = new TableColumn<>("Piantagione");
        piantagioneCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getPiantagioneId() != null ?
                "ID " + cell.getValue().getPiantagioneId().toString() : "N/A"));
        piantagioneCol.setPrefWidth(100);

        TableColumn<Raccolto, String> dataCol = new TableColumn<>("Data Raccolto");
        dataCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getDataRaccolto() != null ?
                cell.getValue().getDataRaccolto().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
        dataCol.setPrefWidth(120);

        TableColumn<Raccolto, String> quantitaCol = new TableColumn<>("Quantità (kg)");
        quantitaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getQuantitaKg() != null ?
                String.format("%.2f", cell.getValue().getQuantitaKg()) : "N/A"));
        quantitaCol.setPrefWidth(120);

        TableColumn<Raccolto, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNote()));
        noteCol.setPrefWidth(250);

        // Colonna stato qualitativo
        TableColumn<Raccolto, String> statoCol = new TableColumn<>("Stato");
        statoCol.setCellValueFactory(cell -> {
            double quantita = cell.getValue().getQuantitaKg() != null ?
                cell.getValue().getQuantitaKg().doubleValue() : 0;
            String stato = quantita > 50 ? "🟢 Ottimo" :
                          quantita > 20 ? "🟡 Buono" :
                          quantita > 0 ? "🟠 Scarso" : "🔴 Vuoto";
            return new javafx.beans.property.SimpleStringProperty(stato);
        });
        statoCol.setPrefWidth(100);

        // Correzione per evitare generic array creation warning
        tableRaccolti.getColumns().clear();
        tableRaccolti.getColumns().add(idCol);
        tableRaccolti.getColumns().add(piantagioneCol);
        tableRaccolti.getColumns().add(dataCol);
        tableRaccolti.getColumns().add(quantitaCol);
        tableRaccolti.getColumns().add(statoCol);
        tableRaccolti.getColumns().add(noteCol);

        tableRaccolti.setItems(raccoltiData);

        // Double-click per modifica
        tableRaccolti.setRowFactory(tv -> {
            TableRow<Raccolto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaRaccolto();
                }
            });
            return row;
        });

        VBox.setVgrow(tableRaccolti, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione filtri
        VBox filtriBox = new VBox(10);
        filtriBox.setPadding(new Insets(15));
        filtriBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label filtriLabel = new Label("🔍 Filtri Raccolti");
        filtriLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane filtriGrid = new GridPane();
        filtriGrid.setHgap(10);
        filtriGrid.setVgap(10);

        filtroPiantagioneCombo.setPromptText("Filtra per piantagione");
        filtroPiantagioneCombo.setPrefWidth(180);
        filtroPiantagioneCombo.setOnAction(e -> {
            if (onFiltroPiantagioneChanged != null) {
                onFiltroPiantagioneChanged.accept(filtroPiantagioneCombo.getValue());
            }
        });

        filtroDataDa.setPromptText("Da data");
        filtroDataDa.setValue(java.time.LocalDate.now().minusMonths(3));
        filtroDataDa.setOnAction(e -> {
            if (onFiltroDataDaChanged != null) {
                onFiltroDataDaChanged.accept(filtroDataDa.getValue());
            }
        });

        filtroDataA.setPromptText("A data");
        filtroDataA.setValue(java.time.LocalDate.now());
        filtroDataA.setOnAction(e -> {
            if (onFiltroDataAChanged != null) {
                onFiltroDataAChanged.accept(filtroDataA.getValue());
            }
        });

        filtroQuantitaMin.setEditable(true);
        filtroQuantitaMin.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (onFiltroQuantitaMinChanged != null) {
                onFiltroQuantitaMinChanged.accept(newVal);
            }
        });

        filtroQuantitaMax.setEditable(true);
        filtroQuantitaMax.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (onFiltroQuantitaMaxChanged != null) {
                onFiltroQuantitaMaxChanged.accept(newVal);
            }
        });

        filtriGrid.add(new Label("Piantagione:"), 0, 0);
        filtriGrid.add(filtroPiantagioneCombo, 1, 0);
        filtriGrid.add(new Label("Periodo:"), 0, 1);
        filtriGrid.add(filtroDataDa, 1, 1);
        filtriGrid.add(new Label("→"), 2, 1);
        filtriGrid.add(filtroDataA, 3, 1);
        filtriGrid.add(new Label("Quantità (kg):"), 0, 2);
        filtriGrid.add(filtroQuantitaMin, 1, 2);
        filtriGrid.add(new Label("→"), 2, 2);
        filtriGrid.add(filtroQuantitaMax, 3, 2);

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

        // Gestione selezione
        tableRaccolti.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });

        pulsantiBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn, aggiornaBtn);
        azioniBox.getChildren().addAll(azioniLabel, pulsantiBox);

        getChildren().addAll(filtriBox, azioniBox, tableRaccolti);
    }

    private void modificaRaccolto() {
        if (getRaccoltoSelezionato() != null) {
            // Il controller gestirà l'apertura del dialog
        }
    }

    // Metodi pubblici per il controller
    public void setRaccolti(java.util.List<Raccolto> raccolti) {
        raccoltiData.setAll(raccolti);
    }

    public Raccolto getRaccoltoSelezionato() {
        return tableRaccolti.getSelectionModel().getSelectedItem();
    }

    // Handler per i pulsanti principali
    public void setOnNuovoRaccolto(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }
    public void setOnModificaRaccolto(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }
    public void setOnEliminaRaccolto(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnAggiornaRaccolti(Consumer<Void> handler) {
        aggiornaBtn.setOnAction(e -> handler.accept(null));
    }

    public void setPiantagioni(java.util.List<String> piantagioni) {
        filtroPiantagioneCombo.getItems().clear();
        filtroPiantagioneCombo.getItems().add("Tutte");
        filtroPiantagioneCombo.getItems().addAll(piantagioni);
        filtroPiantagioneCombo.setValue("Tutte");
    }

    // Metodi per ottenere i valori dei filtri
    public String getFiltroPiantagione() {
        return filtroPiantagioneCombo.getValue();
    }

    public java.time.LocalDate getFiltroDataDa() {
        return filtroDataDa.getValue();
    }

    public java.time.LocalDate getFiltroDataA() {
        return filtroDataA.getValue();
    }

    public double getFiltroQuantitaMin() {
        return filtroQuantitaMin.getValue();
    }

    public double getFiltroQuantitaMax() {
        return filtroQuantitaMax.getValue();
    }

    // Metodi per il controller per impostare le callback
    public void setOnFiltroPiantagioneChanged(java.util.function.Consumer<String> handler) {
        this.onFiltroPiantagioneChanged = handler;
    }
    public void setOnFiltroDataDaChanged(java.util.function.Consumer<java.time.LocalDate> handler) {
        this.onFiltroDataDaChanged = handler;
    }
    public void setOnFiltroDataAChanged(java.util.function.Consumer<java.time.LocalDate> handler) {
        this.onFiltroDataAChanged = handler;
    }
    public void setOnFiltroQuantitaMinChanged(java.util.function.Consumer<Double> handler) {
        this.onFiltroQuantitaMinChanged = handler;
    }
    public void setOnFiltroQuantitaMaxChanged(java.util.function.Consumer<Double> handler) {
        this.onFiltroQuantitaMaxChanged = handler;
    }
}
