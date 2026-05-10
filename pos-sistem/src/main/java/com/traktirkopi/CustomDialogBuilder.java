package com.traktirkopi;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomDialogBuilder {

    public static void showWarning(String titleText, String messageText) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 25, 0, 0, 8);");

        Label icon = new Label("⚠️");
        icon.setStyle("-fx-font-size: 48px; -fx-text-fill: #b3261e;");

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

        Label message = new Label(messageText);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #82746f;");
        message.setWrapText(true);
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        message.setMaxWidth(300);

        Button btnOk = new Button("Got it");
        btnOk.setStyle("-fx-background-color: #536346; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
        btnOk.setOnAction(e -> modal.close());

        root.getChildren().addAll(icon, title, message, btnOk);

        Scene scene = new Scene(root);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }

    public static void showConfirmation(String titleText, String messageText, String confirmBtnText, String confirmBtnColor, Runnable onConfirm) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 25, 0, 0, 8);");

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

        Label message = new Label(messageText);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #82746f;");
        message.setWrapText(true);
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        message.setMaxWidth(300);

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER);

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        btnCancel.setOnAction(e -> modal.close());

        Button btnConfirm = new Button(confirmBtnText);
        btnConfirm.setStyle("-fx-background-color: " + confirmBtnColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
        btnConfirm.setOnAction(e -> {
            modal.close();
            if (onConfirm != null) onConfirm.run();
        });

        btnBox.getChildren().addAll(btnCancel, btnConfirm);

        root.getChildren().addAll(title, message, btnBox);

        Scene scene = new Scene(root);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }
}
