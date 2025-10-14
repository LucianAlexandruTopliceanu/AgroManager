package View;

import DomainModel.Piantagione;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class PiantagioneView extends VBox {
    private final TableView<Piantagione> tablePiantagioni;
    private final ObservableList<Piantagione> piantagioniData;

    // Controlli essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button cambiaStatoBtn;
    private final Button applicaFiltriBtn;
    private final Button resetFiltriBtn;

    // Filtri
    private final ComboBox<String> filtroPiantaCombo;
    private final ComboBox<String> filtroZonaCombo;
    private final ComboBox<String> filtroStatoCombo;
    private final DatePicker filtroDataDa;
    private final DatePicker filtroDataA;

    public PiantagioneView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #F8F9FA;");

        // Inizializzazione componenti
        tablePiantagioni = new TableView<>();
        piantagioniData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuova");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        cambiaStatoBtn = new Button("🔄 Cambia Stato");
        applicaFiltriBtn = new Button("🔍 Applica Filtri");
        resetFiltriBtn = new Button("🔄 Reset");

        filtroPiantaCombo = new ComboBox<>();
        filtroZonaCombo = new ComboBox<>();
        filtroStatoCombo = new ComboBox<>();
        filtroDataDa = new DatePicker();
        filtroDataA = new DatePicker();

        setupTable();
        setupLayout();
    }

    private void setupTable() {
        // Colonne essenziali
        TableColumn<Piantagione, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));

        TableColumn<Piantagione, String> piantaCol = new TableColumn<>("Pianta");
        piantaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getPiantaId() != null ? cell.getValue().getPiantaId().toString() : ""
        ));

        TableColumn<Piantagione, String> zonaCol = new TableColumn<>("Zona");
        zonaCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getZonaId() != null ? cell.getValue().getZonaId().toString() : ""
        ));

        TableColumn<Piantagione, String> statoCol = new TableColumn<>("Stato");
        statoCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getStatoPiantagione() != null ?
                cell.getValue().getStatoPiantagione().getDescrizione() : "N/A"
        ));
        statoCol.setPrefWidth(120);

        TableColumn<Piantagione, String> dataCol = new TableColumn<>("Data");
        dataCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getMessaADimora() != null ?
            cell.getValue().getMessaADimora().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
        ));

        // Correzione per evitare generic array creation warning
        tablePiantagioni.getColumns().clear();
        tablePiantagioni.getColumns().add(idCol);
        tablePiantagioni.getColumns().add(piantaCol);
        tablePiantagioni.getColumns().add(zonaCol);
        tablePiantagioni.getColumns().add(statoCol);
        tablePiantagioni.getColumns().add(dataCol);

        tablePiantagioni.setItems(piantagioniData);

        // Gestione selezione
        tablePiantagioni.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean hasSelection = sel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
            cambiaStatoBtn.setDisable(!hasSelection);
        });
    }

    private void setupLayout() {
        // Sezione filtri in card
        VBox filtriCard = createCard("🔍 Filtri di Ricerca");
        GridPane filtriGrid = new GridPane();
        filtriGrid.setHgap(10);
        filtriGrid.setVgap(10);
        filtriGrid.setPadding(new Insets(10));

        // Configurazione filtri
        filtroPiantaCombo.setPromptText("Tutte le piante");
        filtroZonaCombo.setPromptText("Tutte le zone");
        filtroStatoCombo.setPromptText("Tutti gli stati");
        filtroDataDa.setPromptText("Data da...");
        filtroDataA.setPromptText("Data a...");

        // Layout filtri
        filtriGrid.addRow(0, new Label("Pianta:"), filtroPiantaCombo);
        filtriGrid.addRow(1, new Label("Zona:"), filtroZonaCombo);
        filtriGrid.addRow(2, new Label("Stato:"), filtroStatoCombo);
        filtriGrid.addRow(0, new Label("Da:"), filtroDataDa);
        filtriGrid.addRow(1, new Label("A:"), filtroDataA);

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

        cambiaStatoBtn.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; " +
                               "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        cambiaStatoBtn.setDisable(true);

        // Gestione selezione
        tablePiantagioni.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean hasSelection = sel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
            cambiaStatoBtn.setDisable(!hasSelection);
        });

        azioniBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn, cambiaStatoBtn);
        azioniCard.getChildren().add(azioniBox);

        getChildren().addAll(filtriCard, azioniCard, tablePiantagioni);
        VBox.setVgrow(tablePiantagioni, Priority.ALWAYS);
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
    public void setPiantagioni(java.util.List<Piantagione> piantagioni) {
        piantagioniData.setAll(piantagioni);
    }

    public Piantagione getPiantagioneSelezionata() {
        return tablePiantagioni.getSelectionModel().getSelectedItem();
    }

    // Event handlers
    public void setOnNuovaPiantagione(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }

    public void setOnModificaPiantagione(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }

    public void setOnEliminaPiantagione(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnCambiaStatoPiantagione(Runnable handler) {
        cambiaStatoBtn.setOnAction(e -> handler.run());
    }

    public void setOnApplicaFiltri(Runnable handler) {
        applicaFiltriBtn.setOnAction(e -> handler.run());
    }

    public void setOnResetFiltri(Runnable handler) {
        resetFiltriBtn.setOnAction(e -> handler.run());
    }

    // Gestione filtri
    public void setFiltroPiantaItems(java.util.List<String> items) {
        filtroPiantaCombo.getItems().setAll(items);
    }

    public void setFiltroZonaItems(java.util.List<String> items) {
        filtroZonaCombo.getItems().setAll(items);
    }

    public void setFiltroStatoItems(java.util.List<String> items) {
        filtroStatoCombo.getItems().setAll(items);
    }

    public CriteriFiltro getCriteriFiltro() {
        return new CriteriFiltro(
            filtroPiantaCombo.getValue(),
            filtroZonaCombo.getValue(),
            filtroStatoCombo.getValue(),
            filtroDataDa.getValue(),
            filtroDataA.getValue()
        );
    }

    public void resetFiltri() {
        filtroPiantaCombo.setValue(null);
        filtroZonaCombo.setValue(null);
        filtroStatoCombo.setValue(null);
        filtroDataDa.setValue(null);
        filtroDataA.setValue(null);
    }

    // Metodo per conferma eliminazione
    public boolean confermaEliminazione(Piantagione piantagione) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare la piantagione:");
        alert.setContentText("ID: " + piantagione.getId() +
                            "\nPianta: " + piantagione.getPiantaId() +
                            "\nZona: " + piantagione.getZonaId() +
                            "\n\nQuesta operazione non può essere annullata.");

        return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
    }

    // Record per criteri di filtro
    public record CriteriFiltro(
        String pianta,
        String zona,
        String stato,
        LocalDate dataDa,
        LocalDate dataA
    ) {}
}
