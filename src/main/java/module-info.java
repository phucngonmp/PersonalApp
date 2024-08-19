module org.example.demo {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.hibernate.orm.core;
    requires mysql.connector.j;
    requires jakarta.persistence;
    requires java.naming;
    requires org.kordamp.ikonli.fontawesome5;

    opens org.example.demo to javafx.fxml;
    opens org.example.demo.controllers to javafx.fxml;
    opens org.example.demo.models to org.hibernate.orm.core, javafx.base;

    exports org.example.demo;
    exports org.example.demo.controllers;

}