module com.example.projecttanks {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens com.example.projecttanks to javafx.fxml;
    exports com.example.projecttanks;
}