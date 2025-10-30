module AgroManager {
    requires javafx.controls;
    requires javafx.base;
    requires java.sql;
    requires java.logging;

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
