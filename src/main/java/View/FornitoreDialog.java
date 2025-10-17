package View;

import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import DomainModel.Fornitore;

public class FornitoreDialog extends Stage {
    private final TextField nomeField = new TextField();
    private final TextField indirizzoField = new TextField();
    private final TextField telefonoField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField partitaIvaField = new TextField();
    private final Button salvaBtn = new Button("Salva");
    private final Button annullaBtn = new Button("Annulla");
    private final Fornitore fornitore;
    private boolean confermato = false;

    public FornitoreDialog(Fornitore fornitore) {
        this.fornitore = fornitore != null ? fornitore : new Fornitore();
        setTitle(fornitore == null || fornitore.getId() == null ? "Nuovo Fornitore" : "Modifica Fornitore");
        initModality(Modality.APPLICATION_MODAL);

        // Setup campi
        nomeField.setPromptText("Nome azienda/fornitore");
        indirizzoField.setPromptText("Via, Città, CAP");
        telefonoField.setPromptText("+39 123 456 7890");
        emailField.setPromptText("email@esempio.com");
        partitaIvaField.setPromptText("IT12345678901");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.add(new Label("Nome:*"), 0, row);
        grid.add(nomeField, 1, row++);

        grid.add(new Label("Indirizzo:*"), 0, row);
        grid.add(indirizzoField, 1, row++);

        grid.add(new Label("Telefono:*"), 0, row);
        grid.add(telefonoField, 1, row++);

        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);

        grid.add(new Label("Partita IVA:"), 0, row);
        grid.add(partitaIvaField, 1, row++);

        // Pulsanti
        var buttonBox = new javafx.scene.layout.HBox(10);
        buttonBox.getChildren().addAll(salvaBtn, annullaBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, row);

        salvaBtn.getStyleClass().add("btn-primary");
        annullaBtn.getStyleClass().add("btn-secondary");

        // Popola campi se modifica
        if (this.fornitore != null && this.fornitore.getId() != null) {
            nomeField.setText(this.fornitore.getNome());
            indirizzoField.setText(this.fornitore.getIndirizzo());
            telefonoField.setText(this.fornitore.getNumeroTelefono());
            emailField.setText(this.fornitore.getEmail());
            partitaIvaField.setText(this.fornitore.getPartitaIva());
        }

        salvaBtn.setOnAction(e -> {
            if (validaInput()) {
                aggiornaFornitore();
                confermato = true;
                close();
            } else {
                NotificationHelper.showError("Errore validazione",
                    "Compila tutti i campi obbligatori (*).\nVerifica che l'email sia valida.");
            }
        });

        annullaBtn.setOnAction(e -> close());

        setScene(new Scene(grid, 400, 300));
    }

    private void aggiornaFornitore() {
        fornitore.setNome(nomeField.getText().trim());
        fornitore.setIndirizzo(indirizzoField.getText().trim());
        fornitore.setNumeroTelefono(telefonoField.getText().trim());

        String email = emailField.getText().trim();
        fornitore.setEmail(email.isEmpty() ? null : email);

        String piva = partitaIvaField.getText().trim();
        fornitore.setPartitaIva(piva.isEmpty() ? null : piva);
    }

    private boolean validaInput() {
        if (nomeField.getText().trim().isEmpty()) return false;
        if (indirizzoField.getText().trim().isEmpty()) return false;
        if (telefonoField.getText().trim().isEmpty()) return false;

        // Validazione email se presente
        String email = emailField.getText().trim();
        if (!email.isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return false;
            }
        }

        return true;
    }

    public Fornitore getFornitore() { return fornitore; }
    public boolean isConfermato() { return confermato; }
}
