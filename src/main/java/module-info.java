module AgroManager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;
    // aggiungi altri requires se servono
    exports Main to javafx.graphics, javafx.fxml;
    exports Controller to javafx.fxml;
    exports DomainModel;
    exports ORM;
    exports View to javafx.fxml;
    exports BusinessLogic.Service;
    exports BusinessLogic.Strategy;
    exports BusinessLogic;
    exports BusinessLogic.Exception;

    opens Main to javafx.fxml, javafx.graphics;
    opens Controller to javafx.fxml;
    opens View to javafx.fxml;
}
