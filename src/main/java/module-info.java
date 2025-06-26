module com.example.projecttanks {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;
    requires java.desktop;
    requires javafx.media;

    opens com.example.projecttanks to javafx.fxml;
    exports com.example.projecttanks;
}