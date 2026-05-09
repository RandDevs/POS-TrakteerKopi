module com.traktirkopi {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.traktirkopi to javafx.fxml;
    exports com.traktirkopi;
}
