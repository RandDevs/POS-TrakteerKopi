package com.traktirkopi;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Dummy validation — just check fields are not empty
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("⚠ Please enter both username and password.");
            return;
        }

        try {
            App.setRoot("primary");
        } catch (IOException e) {
            errorLabel.setText("⚠ Failed to load the application.");
            e.printStackTrace();
        }
    }
}
