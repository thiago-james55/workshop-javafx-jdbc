module application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens application to javafx.fxml;
    exports application;

    opens controller to javafx.fxml;
    exports controller;

    opens db to javafx.fxml;
    exports db;

    opens listeners to javafx.fxml;
    exports listeners;

    opens model.dao to javafx.fxml;
    exports model.dao;

    opens model.dao.impl to javafx.fxml;
    exports model.dao.impl;

    opens model.entities to javafx.fxml;
    exports model.entities;

    opens model.services to javafx.fxml;
    exports model.services;

    opens util to javafx.fxml;
    exports util;

}