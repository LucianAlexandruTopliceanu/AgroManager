package View;

import DomainModel.Fornitore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class FornitoreView extends VBox {

    private final TableView<Fornitore> tableFornitori;
    private final ObservableList<Fornitore> fornitoriData;

    // Pulsanti essenziali
    private final Button nuovoBtn;
    private final Button modificaBtn;
    private final Button eliminaBtn;
    private final Button aggiornaBtn;

    // Controlli di ricerca
    private final TextField ricercaNomeField;
    private final TextField ricercaCittaField;

    // Callback per notificare il controller sui cambiamenti dei filtri
    private java.util.function.Consumer<String> onTestoRicercaNomeChanged;
    private java.util.function.Consumer<String> onTestoRicercaCittaChanged;

    public FornitoreView() {
        tableFornitori = new TableView<>();
        fornitoriData = FXCollections.observableArrayList();
        nuovoBtn = new Button("➕ Nuovo Fornitore");
        modificaBtn = new Button("✏️ Modifica");
        eliminaBtn = new Button("🗑️ Elimina");
        aggiornaBtn = new Button("🔄 Aggiorna");
        ricercaNomeField = new TextField();
        ricercaCittaField = new TextField();

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
        TableColumn<Fornitore, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setPrefWidth(60);

        TableColumn<Fornitore, String> nomeCol = new TableColumn<>("Nome Fornitore");
        nomeCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNome()));
        nomeCol.setPrefWidth(200);

        TableColumn<Fornitore, String> indirizzoCol = new TableColumn<>("Indirizzo");
        indirizzoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getIndirizzo()));
        indirizzoCol.setPrefWidth(250);

        TableColumn<Fornitore, String> telefonoCol = new TableColumn<>("Telefono");
        telefonoCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getNumeroTelefono()));
        telefonoCol.setPrefWidth(120);

        TableColumn<Fornitore, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        emailCol.setPrefWidth(180);

        TableColumn<Fornitore, String> pivaCol = new TableColumn<>("P.IVA");
        pivaCol.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getPartitaIva()));
        pivaCol.setPrefWidth(120);

        // Correzione per evitare generic array creation warning
        tableFornitori.getColumns().clear();
        tableFornitori.getColumns().add(idCol);
        tableFornitori.getColumns().add(nomeCol);
        tableFornitori.getColumns().add(indirizzoCol);
        tableFornitori.getColumns().add(telefonoCol);
        tableFornitori.getColumns().add(emailCol);
        tableFornitori.getColumns().add(pivaCol);

        tableFornitori.setItems(fornitoriData);

        // Double-click per modifica
        tableFornitori.setRowFactory(tv -> {
            TableRow<Fornitore> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    modificaFornitore();
                }
            });
            return row;
        });

        VBox.setVgrow(tableFornitori, Priority.ALWAYS);
    }

    private void setupControls() {
        // Sezione ricerca
        VBox ricercaBox = new VBox(10);
        ricercaBox.setPadding(new Insets(15));
        ricercaBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label ricercaLabel = new Label("🔍 Ricerca Fornitori");
        ricercaLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox ricercaControls = new HBox(10);
        ricercaNomeField.setPromptText("Cerca per nome...");
        ricercaNomeField.setPrefWidth(200);
        ricercaNomeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (onTestoRicercaNomeChanged != null) {
                onTestoRicercaNomeChanged.accept(newVal);
            }
        });

        ricercaCittaField.setPromptText("Cerca per città...");
        ricercaCittaField.setPrefWidth(150);
        ricercaCittaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (onTestoRicercaCittaChanged != null) {
                onTestoRicercaCittaChanged.accept(newVal);
            }
        });

        ricercaControls.getChildren().addAll(
            new Label("Nome:"), ricercaNomeField,
            new Label("Città:"), ricercaCittaField
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
        tableFornitori.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean hasSelection = newSel != null;
            modificaBtn.setDisable(!hasSelection);
            eliminaBtn.setDisable(!hasSelection);
        });

        pulsantiBox.getChildren().addAll(nuovoBtn, modificaBtn, eliminaBtn, aggiornaBtn);
        azioniBox.getChildren().addAll(azioniLabel, pulsantiBox);

        getChildren().addAll(ricercaBox, azioniBox, tableFornitori);
    }

    private void modificaFornitore() {
        if (getFornitoreSelezionato() != null) {
            // Il controller gestirà l'apertura del dialog
        }
    }

    // Metodi pubblici per il controller
    public void setFornitori(java.util.List<Fornitore> fornitori) {
        fornitoriData.setAll(fornitori);
    }

    public Fornitore getFornitoreSelezionato() {
        return tableFornitori.getSelectionModel().getSelectedItem();
    }

    // Handler per i pulsanti principali
    public void setOnNuovoFornitore(Runnable handler) {
        nuovoBtn.setOnAction(e -> handler.run());
    }
    public void setOnModificaFornitore(Runnable handler) {
        modificaBtn.setOnAction(e -> handler.run());
    }
    public void setOnEliminaFornitore(Runnable handler) {
        eliminaBtn.setOnAction(e -> handler.run());
    }

    public void setOnAggiornaFornitori(Consumer<Void> handler) {
        aggiornaBtn.setOnAction(e -> handler.accept(null));
    }

    // Metodi per il controller per impostare le callback
    public void setOnTestoRicercaNomeChanged(java.util.function.Consumer<String> handler) {
        this.onTestoRicercaNomeChanged = handler;
    }
    public void setOnTestoRicercaCittaChanged(java.util.function.Consumer<String> handler) {
        this.onTestoRicercaCittaChanged = handler;
    }
}
