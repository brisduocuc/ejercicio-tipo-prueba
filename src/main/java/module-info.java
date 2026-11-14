module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens com.example.demo1 to javafx.fxml;
    opens com.example.demo1.model to com.fasterxml.jackson.databind;

    exports com.example.demo1;
}