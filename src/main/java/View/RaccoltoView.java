package View;

import DomainModel.Raccolto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class RaccoltoView extends VBox {

    private final TableView<Raccolto> tableRaccolti;
    private final ObservableList<Raccolto> raccoltiData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button applicaFiltriBtn;
    private final Button resetFiltriBtn;

    // Controlli di ricerca e filtro
    private final ComboBox<String> filtroPiantagioneCombo;
    private final DatePicker filtroDataDa;
    private final DatePicker filtroDataA;
    private final Spinner<Double> filtroQuantitaMin;
    private final Spinner<Double> filtroQuantitaMax;

    public RaccoltoView() {
        tableRaccolti = new TableView<>();
        raccoltiData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuovo Raccolto");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        applicaFiltriBtn = new Button("🔍 Applica Filtri");
        resetFiltriBtn = new Button("🔄 Reset Filtri");
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

        tableRaccolti.getColumns().clear();
        tableRaccolti.getColumns().addAll(idCol, piantagioneCol, dataCol, quantitaCol, statoCol, noteCol);
        tableRaccolti.setItems(raccoltiData);

        // Double-click per modifica
        tableRaccolti.setRowFactory(tv -> {
            TableRow<Raccolto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    // Il controller gestirà l'apertura del dialog
                }
            });
            return row;
        });

        VBox.setVgrow(tableRaccolti, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione filtri in card
        VBox filtriCard = createCard("🔍 Filtri di Ricerca");
        GridPane filtriGrid = new GridPane();
        filtriGrid.setHgap(10);
        filtriGrid.setVgap(10);

        filtroPiantagioneCombo.setPromptText("Tutte le piantagioni");
        filtroDataDa.setPromptText("Data da...");
        filtroDataA.setPromptText("Data a...");
        filtroQuantitaMin.setEditable(true);
        filtroQuantitaMax.setEditable(true);

        filtriGrid.addRow(0, new Label("Piantagione:"), filtroPiantagioneCombo);
        filtriGrid.addRow(1, new Label("Data da:"), filtroDataDa);
        filtriGrid.addRow(2, new Label("Data a:"), filtroDataA);
        filtriGrid.addRow(0, new Label("Quantità min (kg):"), filtroQuantitaMin);
        filtriGrid.addRow(1, new Label("Quantità max (kg):"), filtroQuantitaMax);

        HBox filtriActions = new HBox(10);
        applicaFiltriBtn.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        resetFiltriBtn.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; " +
                               "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        filtriActions.getChildren().addAll(applicaFiltriBtn, resetFiltriBtn);

        filtriCard.getChildren().addAll(filtriGrid, filtriActions);

        // Sezione azioni in card
        VBox azioniCard = createCard("⚡ Azioni");
        HBox azioniBox = new HBox(10);

        // Stili pulsanti
        nuovoBtn.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; " +
                         "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");

        modificaBtn.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        modificaBtn.setDisable(true);

        eliminaBtn.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        eliminaBtn.setDisable(true);

        // Gestione selezione
        tableRaccolti.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });

        azioniBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn);
        azioniCard.getChildren().add(azioniBox);

        getChildren().addAll(filtriCard, azioniCard, tableRaccolti);
    }

    private VBox createCard(String title) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        card.getChildren().add(titleLabel);

        return card;
    }

    // Metodi pubblici per il controller
    public void setRaccolti(java.util.List<Raccolto> raccolti) {
        raccoltiData.setAll(raccolti);
    }

    public Raccolto getRaccoltoSelezionato() {
        return tableRaccolti.getSelectionModel().getSelectedItem();
    }

    // Event handlers
    public void setOnNuovoRaccolto(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }

    public void setOnModificaRaccolto(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }

    public void setOnEliminaRaccolto(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnApplicaFiltri(Runnable handler) {
        applicaFiltriBtn.setOnAction(e -> handler.run());
    }

    public void setOnResetFiltri(Runnable handler) {
        resetFiltriBtn.setOnAction(e -> handler.run());
    }

    // Gestione filtri
    public void setPiantagioni(java.util.List<String> piantagioni) {
        filtroPiantagioneCombo.getItems().setAll(piantagioni);
    }

    public CriteriFiltro getCriteriFiltro() {
        return new CriteriFiltro(
            filtroPiantagioneCombo.getValue(),
            filtroDataDa.getValue(),
            filtroDataA.getValue(),
            filtroQuantitaMin.getValue(),
            filtroQuantitaMax.getValue()
        );
    }

    public void resetFiltri() {
        filtroPiantagioneCombo.setValue(null);
        filtroDataDa.setValue(null);
        filtroDataA.setValue(null);
        filtroQuantitaMin.getValueFactory().setValue(0.0);
        filtroQuantitaMax.getValueFactory().setValue(1000.0);
    }

    // Metodo per conferma eliminazione
    public boolean confermaEliminazione(Raccolto raccolto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare il raccolto:");
        alert.setContentText("ID: " + raccolto.getId() +
                            "\nPiantagione: " + raccolto.getPiantagioneId() +
                            "\nQuantità: " + raccolto.getQuantitaKg() + " kg" +
                            "\n\nQuesta operazione non può essere annullata.");

        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    // Record per criteri di filtro
    public record CriteriFiltro(
        String piantagione,
        LocalDate dataDa,
        LocalDate dataA,
        Double quantitaMin,
        Double quantitaMax
    ) {}
}
