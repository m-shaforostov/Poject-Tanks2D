module com.example.projecttanks {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projecttanks to javafx.fxml;
    exports com.example.projecttanks;
}