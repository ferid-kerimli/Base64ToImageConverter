module com.example.base64toimageconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.base64toimageconverter to javafx.fxml;
    exports com.example.base64toimageconverter;
}