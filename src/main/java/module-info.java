module com.example.plzz {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;

    opens com.example.plzz to javafx.fxml;
    exports com.example.plzz;
}
