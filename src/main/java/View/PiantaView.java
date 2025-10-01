package View;

import DomainModel.Pianta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class PiantaView extends VBox {

    private final TableView<Pianta> tablePiante;
    private final ObservableList<Pianta> pianteData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button aggiornaBtn;

    // Controlli di ricerca e filtro
    private final TextField ricercaTipoField;
    private final TextField ricercaVarietaField;
    private final ComboBox<String> filtroFornitoreCombo;

    // Callback per notificare il controller sui cambiamenti dei filtri
    private java.util.function.Consumer<String> onTestoRicercaTipoChanged;
    private java.util.function.Consumer<String> onTestoRicercaVarietaChanged;
    private java.util.function.Consumer<String> onFiltroFornitoreChanged;

    public PiantaView() {
        tablePiante = new TableView<>();
        pianteData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuova Pianta");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        aggiornaBtn = new Button("🔄 Aggiorna");
        ricercaTipoField = new TextField();
        ricercaVarietaField = new TextField();
        filtroFornitoreCombo = new ComboBox<>();

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
        TableColumn<Pianta, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Pianta, String> tipoCol = new TableColumn<>("Tipo");
        tipoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipo()));
        tipoCol.setPrefWidth(150);

        TableColumn<Pianta, String> varietaCol = new TableColumn<>("Varietà");
        varietaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getVarieta()));
        varietaCol.setPrefWidth(150);

        TableColumn<Pianta, String> costoCol = new TableColumn<>("Costo (€)");
        costoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getCosto() != null ?
                String.format("%.2f", cell.getValue().getCosto()) : "N/A"));
        costoCol.setPrefWidth(100);

        TableColumn<Pianta, String> fornitoreCol = new TableColumn<>("Fornitore ID");
        fornitoreCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getFornitoreId() != null ?
                cell.getValue().getFornitoreId().toString() : "N/A"));
        fornitoreCol.setPrefWidth(100);

        TableColumn<Pianta, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNote()));
        noteCol.setPrefWidth(200);

        // Correzione per evitare generic array creation warning
        tablePiante.getColumns().clear();
        tablePiante.getColumns().add(idCol);
        tablePiante.getColumns().add(tipoCol);
        tablePiante.getColumns().add(varietaCol);
        tablePiante.getColumns().add(costoCol);
        tablePiante.getColumns().add(fornitoreCol);
        tablePiante.getColumns().add(noteCol);

        tablePiante.setItems(pianteData);

        // Double-click per modifica
        tablePiante.setRowFactory(tv -> {
            TableRow<Pianta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaPianta();
                }
            });
            return row;
        });

        VBox.setVgrow(tablePiante, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione ricerca
        VBox ricercaBox = new VBox(10);
        ricercaBox.setPadding(new Insets(15));
        ricercaBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label ricercaLabel = new Label("🔍 Ricerca Piante");
        ricercaLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox ricercaControls = new HBox(10);
        ricercaTipoField.setPromptText("Cerca per tipo...");
        ricercaTipoField.setPrefWidth(150);
        ricercaTipoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (onTestoRicercaTipoChanged != null) {
                onTestoRicercaTipoChanged.accept(newVal);
            }
        });

        ricercaVarietaField.setPromptText("Cerca per varietà...");
        ricercaVarietaField.setPrefWidth(150);
        ricercaVarietaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (onTestoRicercaVarietaChanged != null) {
                onTestoRicercaVarietaChanged.accept(newVal);
            }
        });

        filtroFornitoreCombo.setPromptText("Filtra per fornitore");
        filtroFornitoreCombo.setPrefWidth(200);
        filtroFornitoreCombo.setOnAction(e -> {
            if (onFiltroFornitoreChanged != null) {
                onFiltroFornitoreChanged.accept(filtroFornitoreCombo.getValue());
            }
        });

        ricercaControls.getChildren().addAll(
            new Label("Tipo:"), ricercaTipoField,
            new Label("Varietà:"), ricercaVarietaField,
            new Label("Fornitore:"), filtroFornitoreCombo
        );

        ricercaBox.getChildren().addAll(ricercaLabel, ricercaControls);

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
        tablePiante.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });

        pulsantiBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn, aggiornaBtn);
        azioniBox.getChildren().addAll(azioniLabel, pulsantiBox);

        getChildren().addAll(ricercaBox, azioniBox, tablePiante);
    }

    private void modificaPianta() {
        if (getPiantaSelezionata() != null) {
            // Il controller gestirà l'apertura del dialog
        }
    }

    // Metodi pubblici per il controller
    public void setPiante(java.util.List<Pianta> piante) {
        pianteData.setAll(piante);
    }

    public Pianta getPiantaSelezionata() {
        return tablePiante.getSelectionModel().getSelectedItem();
    }

    // Handler per i pulsanti principali
    public void setOnNuovaPianta(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }
    public void setOnModificaPianta(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }
    public void setOnEliminaPianta(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnAggiornaPiante(Consumer<Void> handler) {
        aggiornaBtn.setOnAction(e -> handler.accept(null));
    }

    public String getTestoRicercaTipo() {
        return ricercaTipoField.getText();
    }

    public String getTestoRicercaVarieta() {
        return ricercaVarietaField.getText();
    }

    public String getFiltroFornitore() {
        return filtroFornitoreCombo.getValue();
    }

    public void setFornitori(java.util.List<String> fornitori) {
        filtroFornitoreCombo.getItems().clear();
        filtroFornitoreCombo.getItems().add("Tutti");
        filtroFornitoreCombo.getItems().addAll(fornitori);
        filtroFornitoreCombo.setValue("Tutti");
    }

    // Metodi per il controller per impostare le callback
    public void setOnTestoRicercaTipoChanged(java.util.function.Consumer<String> handler) {
        this.onTestoRicercaTipoChanged = handler;
    }
    public void setOnTestoRicercaVarietaChanged(java.util.function.Consumer<String> handler) {
        this.onTestoRicercaVarietaChanged = handler;
    }
    public void setOnFiltroFornitoreChanged(java.util.function.Consumer<String> handler) {
        this.onFiltroFornitoreChanged = handler;
    }
}
