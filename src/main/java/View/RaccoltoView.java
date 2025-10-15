package View;

import DomainModel.Raccolto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

/**
 * View moderna per la gestione dei raccolti.
 * Stile coerente con l'applicazione.
 */
public class RaccoltoView extends VBox {

    // Componenti UI
    private final TableView<Raccolto> tableRaccolti = new TableView<>();
    private final ObservableList<Raccolto> raccoltiData = FXCollections.observableArrayList();

    // Pulsanti azione
    private final Button nuovoBtn = new Button("➕ Nuovo Raccolto");
    private final Button modificaBtn = new Button("✏️ Modifica");
    private final Button eliminaBtn = new Button("🗑️ Elimina");
    private final Button applicaFiltriBtn = new Button("🔍 Applica Filtri");
    private final Button resetFiltriBtn = new Button("🔄 Reset");

    // Controlli ricerca
    private final ComboBox<String> filtroPiantagioneCombo = new ComboBox<>();
    private final DatePicker filtroDataDa = new DatePicker();
    private final DatePicker filtroDataA = new DatePicker();
    private final Spinner<Double> filtroQuantitaMin = new Spinner<>(0.0, 1000.0, 0.0, 0.1);
    private final Spinner<Double> filtroQuantitaMax = new Spinner<>(0.0, 1000.0, 1000.0, 0.1);

    public RaccoltoView() {
        setupStyles();
        setupLayout();
        setupTable();
    }

    private void setupStyles() {
        getStyleClass().add("main-container");

        // Configurazione controlli ricerca
        filtroPiantagioneCombo.setPromptText("Tutte le piantagioni");
        filtroPiantagioneCombo.setPrefWidth(200);
        filtroPiantagioneCombo.getStyleClass().add("combo-box-standard");

        filtroDataDa.setPromptText("Data da");
        filtroDataDa.setPrefWidth(150);
        filtroDataDa.getStyleClass().add("date-picker-standard");

        filtroDataA.setPromptText("Data a");
        filtroDataA.setPrefWidth(150);
        filtroDataA.getStyleClass().add("date-picker-standard");

        filtroQuantitaMin.setEditable(true);
        filtroQuantitaMin.setPrefWidth(100);
        filtroQuantitaMin.getStyleClass().add("spinner-standard");

        filtroQuantitaMax.setEditable(true);
        filtroQuantitaMax.setPrefWidth(100);
        filtroQuantitaMax.getStyleClass().add("spinner-standard");

        // Configurazione bottoni
        nuovoBtn.getStyleClass().add("btn-primary");
        modificaBtn.getStyleClass().add("btn-secondary");
        modificaBtn.setDisable(true);
        eliminaBtn.getStyleClass().add("btn-danger");
        eliminaBtn.setDisable(true);
        applicaFiltriBtn.getStyleClass().add("btn-secondary");
        resetFiltriBtn.getStyleClass().add("btn-support");

        // Configurazione tabella
        tableRaccolti.setPlaceholder(new Label("Nessun raccolto registrato. Aggiungi il primo raccolto!"));
    }

    private void setupLayout() {
        VBox header = createHeader();
        VBox ricercaCard = createRicercaSection();
        HBox actionBar = createActionBar();
        VBox tableCard = createTableSection();

        getChildren().addAll(header, ricercaCard, actionBar, tableCard);
        VBox.setVgrow(tableCard, Priority.ALWAYS);
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header-section");

        Label title = new Label("🧺 Gestione Raccolti");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Registra e monitora i raccolti delle piantagioni");
        subtitle.getStyleClass().add("subtitle");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createRicercaSection() {
        VBox card = new VBox(15);
        card.getStyleClass().add("styled-card");

        Label cardTitle = new Label("🔍 Filtri Ricerca");
        cardTitle.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.getStyleClass().add("input-grid");

        Label piantagioneLabel = new Label("Piantagione:");
        piantagioneLabel.getStyleClass().add("field-label");

        Label dataDaLabel = new Label("Data da:");
        dataDaLabel.getStyleClass().add("field-label");

        Label dataALabel = new Label("Data a:");
        dataALabel.getStyleClass().add("field-label");

        Label quantitaMinLabel = new Label("Quantità min (kg):");
        quantitaMinLabel.getStyleClass().add("field-label");

        Label quantitaMaxLabel = new Label("Quantità max (kg):");
        quantitaMaxLabel.getStyleClass().add("field-label");

        grid.add(piantagioneLabel, 0, 0);
        grid.add(filtroPiantagioneCombo, 1, 0);
        grid.add(dataDaLabel, 2, 0);
        grid.add(filtroDataDa, 3, 0);
        grid.add(dataALabel, 0, 1);
        grid.add(filtroDataA, 1, 1);
        grid.add(quantitaMinLabel, 2, 1);
        grid.add(filtroQuantitaMin, 3, 1);
        grid.add(quantitaMaxLabel, 0, 2);
        grid.add(filtroQuantitaMax, 1, 2);

        card.getChildren().addAll(cardTitle, grid);
        return card;
    }

    private HBox createActionBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 10, 0));

        HBox mainGroup = new HBox(10);
        mainGroup.setAlignment(Pos.CENTER_LEFT);
        mainGroup.getChildren().add(nuovoBtn);

        HBox secondaryGroup = new HBox(8);
        secondaryGroup.setAlignment(Pos.CENTER_LEFT);
        secondaryGroup.getChildren().addAll(modificaBtn, eliminaBtn);

        HBox filterGroup = new HBox(8);
        filterGroup.setAlignment(Pos.CENTER_LEFT);
        filterGroup.getChildren().addAll(applicaFiltriBtn, resetFiltriBtn);

        Separator sep1 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep1.getStyleClass().add("v-separator");
        Separator sep2 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep2.getStyleClass().add("v-separator");

        bar.getChildren().addAll(mainGroup, sep1, secondaryGroup, sep2, filterGroup);
        return bar;
    }

    private VBox createTableSection() {
        VBox card = new VBox(12);
        card.getStyleClass().add("styled-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        Label cardTitle = new Label("📋 Elenco Raccolti");
        cardTitle.getStyleClass().add("card-title");

        VBox.setVgrow(tableRaccolti, Priority.ALWAYS);

        card.getChildren().addAll(cardTitle, tableRaccolti);
        return card;
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

        tableRaccolti.setRowFactory(tv -> {
            TableRow<Raccolto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaRaccolto();
                }
            });
            return row;
        });

        tableRaccolti.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });
    }

    private void modificaRaccolto() {
        if (getRaccoltoSelezionato() != null) {
            // Il controller gestirà l'apertura del dialog
        }
    }

    // Metodi pubblici
    public void setRaccolti(java.util.List<Raccolto> raccolti) {
        raccoltiData.setAll(raccolti);
    }

    public Raccolto getRaccoltoSelezionato() {
        return tableRaccolti.getSelectionModel().getSelectedItem();
    }

    public void setPiantagioni(java.util.List<String> piantagioni) {
        filtroPiantagioneCombo.getItems().clear();
        filtroPiantagioneCombo.getItems().add("Tutte le piantagioni");
        filtroPiantagioneCombo.getItems().addAll(piantagioni);
        filtroPiantagioneCombo.getSelectionModel().selectFirst();
    }

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

    public CriteriFiltro getCriteriFiltro() {
        String piantagione = filtroPiantagioneCombo.getValue();
        if (piantagione != null && piantagione.equals("Tutte le piantagioni")) {
            piantagione = null;
        }
        return new CriteriFiltro(
            piantagione,
            filtroDataDa.getValue(),
            filtroDataA.getValue(),
            filtroQuantitaMin.getValue(),
            filtroQuantitaMax.getValue()
        );
    }

    public void resetFiltri() {
        filtroPiantagioneCombo.getSelectionModel().selectFirst();
        filtroDataDa.setValue(null);
        filtroDataA.setValue(null);
        filtroQuantitaMin.getValueFactory().setValue(0.0);
        filtroQuantitaMax.getValueFactory().setValue(1000.0);
    }

    public boolean confermaEliminazione(Raccolto raccolto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare il raccolto:");
        alert.setContentText("Data: " + raccolto.getDataRaccolto() +
                            "\nQuantità: " + raccolto.getQuantitaKg() + " kg" +
                            "\n\nQuesta operazione non può essere annullata.");
        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    public record CriteriFiltro(String piantagione, LocalDate dataDa, LocalDate dataA,
                                Double quantitaMin, Double quantitaMax) {}
}
