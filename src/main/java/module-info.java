module application {
    requires javafx.controls;
    requires javafx.fxml;


    opens application to javafx.fxml;
    exports application;

    opens controller to javafx.fxml;
    exports controller;

    opens util to javafx.fxml;
    exports util;
}