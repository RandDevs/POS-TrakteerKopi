package com.traktirkopi;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ShiftController {

    @FXML private VBox closedView;
    @FXML private VBox openView;
    @FXML private Label lblStartTime;

    private PrimaryController primaryController;

    public void setPrimaryController(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }

    @FXML
    public void initialize() {
        refreshView();
    }

    private void refreshView() {
        if (DataStore.isShiftOpen) {
            closedView.setVisible(false);
            closedView.setManaged(false);
            openView.setVisible(true);
            openView.setManaged(true);
            lblStartTime.setText("Start Time: " + DataStore.shiftStartTime);
        } else {
            closedView.setVisible(true);
            closedView.setManaged(true);
            openView.setVisible(false);
            openView.setManaged(false);
        }
    }

    @FXML
    private void startShift() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        Label title = new Label("Start Shift");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

        VBox inputGrp = new VBox(8);
        inputGrp.setAlignment(Pos.CENTER_LEFT);
        Label lblCash = new Label("Starting Drawer Cash (Modal Awal)");
        lblCash.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #50443f;");
        
        TextField cashInput = new TextField();
        cashInput.setPromptText("Rp 0");
        cashInput.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 16px;");
        // Strict numeric formatter
        cashInput.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

        inputGrp.getChildren().addAll(lblCash, cashInput);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #b3261e; -fx-font-size: 12px;");

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        btnCancel.setOnAction(e -> modal.close());

        Button btnStart = new Button("Confirm");
        btnStart.setStyle("-fx-background-color: #536346; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
        btnStart.setOnAction(e -> {
            if (cashInput.getText().isEmpty()) {
                errorLabel.setText("⚠ Please enter starting cash.");
                return;
            }
            try {
                DataStore.startingCash = Double.parseDouble(cashInput.getText());
                DataStore.isShiftOpen = true;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
                DataStore.shiftStartTime = LocalDateTime.now().format(formatter);
                
                refreshView();
                if (primaryController != null) {
                    primaryController.refreshPosViewState();
                }
                modal.close();
            } catch (NumberFormatException ex) {
                errorLabel.setText("⚠ Invalid number.");
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        btnBox.getChildren().addAll(spacer, btnCancel, btnStart);

        root.getChildren().addAll(title, inputGrp, errorLabel, btnBox);

        Scene scene = new Scene(root, 360, 260);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }

    @FXML
    private void endShift() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        Label title = new Label("End Shift");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

        // STEP 1: Blind Input
        VBox step1Box = new VBox(15);
        step1Box.setAlignment(Pos.CENTER);

        VBox inputGrp = new VBox(8);
        inputGrp.setAlignment(Pos.CENTER_LEFT);
        Label lblCash = new Label("Actual Cash in Drawer");
        lblCash.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #50443f;");
        
        TextField cashInput = new TextField();
        cashInput.setPromptText("Rp 0");
        cashInput.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 16px;");
        cashInput.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")) return change;
            return null;
        }));
        inputGrp.getChildren().addAll(lblCash, cashInput);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #b3261e; -fx-font-size: 12px;");

        HBox btnBox1 = new HBox(12);
        btnBox1.setAlignment(Pos.CENTER_RIGHT);
        Button btnCancel1 = new Button("Cancel");
        btnCancel1.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        btnCancel1.setOnAction(e -> modal.close());
        Button btnCalc = new Button("Calculate");
        btnCalc.setStyle("-fx-background-color: #715547; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        btnBox1.getChildren().addAll(spacer1, btnCancel1, btnCalc);

        step1Box.getChildren().addAll(inputGrp, errorLabel, btnBox1);

        // STEP 2: Summary
        VBox step2Box = new VBox(15);
        step2Box.setAlignment(Pos.CENTER);
        step2Box.setVisible(false);
        step2Box.setManaged(false);

        VBox summaryContainer = new VBox(10);
        summaryContainer.setStyle("-fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: white;");

        btnCalc.setOnAction(e -> {
            if (cashInput.getText().isEmpty()) {
                errorLabel.setText("⚠ Please enter actual cash.");
                return;
            }
            try {
                double actualCash = Double.parseDouble(cashInput.getText());
                
                // Calculate
                double totalCashSales = 0;
                double totalQrisSales = 0;
                for (Transaction tx : DataStore.transactions) {
                    if ("Cash".equals(tx.getPaymentMethod())) totalCashSales += tx.getTotalAmount();
                    if ("QRIS".equals(tx.getPaymentMethod())) totalQrisSales += tx.getTotalAmount();
                }

                double expectedCash = DataStore.startingCash + totalCashSales;
                double difference = actualCash - expectedCash;

                summaryContainer.getChildren().clear();
                summaryContainer.getChildren().addAll(
                    createSummaryRow("Total Cash Sales:", totalCashSales, false),
                    createSummaryRow("Total QRIS Sales:", totalQrisSales, false),
                    createSummaryRow("Starting Cash:", DataStore.startingCash, false),
                    createSummaryRow("Expected Cash:", expectedCash, true),
                    createSummaryRow("Actual Cash:", actualCash, true)
                );

                HBox diffRow = new HBox();
                Label diffLbl = new Label("Difference:");
                diffLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");
                Label diffVal = new Label(String.format(Locale.US, "Rp %,.0f", difference));
                if (difference >= 0) {
                    diffVal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #536346;"); // Green
                } else {
                    diffVal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #b3261e;"); // Red
                }
                Region diffSpacer = new Region();
                HBox.setHgrow(diffSpacer, Priority.ALWAYS);
                diffRow.getChildren().addAll(diffLbl, diffSpacer, diffVal);
                
                summaryContainer.getChildren().add(new Label("")); // Spacer
                summaryContainer.getChildren().add(diffRow);

                step1Box.setVisible(false);
                step1Box.setManaged(false);
                step2Box.setVisible(true);
                step2Box.setManaged(true);

            } catch (NumberFormatException ex) {
                errorLabel.setText("⚠ Invalid number.");
            }
        });

        HBox btnBox2 = new HBox(12);
        btnBox2.setAlignment(Pos.CENTER_RIGHT);
        Button btnCancel2 = new Button("Cancel");
        btnCancel2.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        btnCancel2.setOnAction(e -> modal.close());

        Button btnConfirmEnd = new Button("Confirm End Shift");
        btnConfirmEnd.setStyle("-fx-background-color: #b3261e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
        btnConfirmEnd.setOnAction(e -> {
            DataStore.isShiftOpen = false;
            DataStore.startingCash = 0;
            DataStore.shiftStartTime = null;
            DataStore.transactions.clear();
            
            refreshView();
            if (primaryController != null) {
                primaryController.refreshPosViewState();
            }
            modal.close();
        });

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        btnBox2.getChildren().addAll(spacer2, btnCancel2, btnConfirmEnd);

        step2Box.getChildren().addAll(summaryContainer, btnBox2);

        root.getChildren().addAll(title, step1Box, step2Box);

        Scene scene = new Scene(root, 400, 420);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private HBox createSummaryRow(String label, double amount, boolean isBold) {
        HBox row = new HBox();
        Label l = new Label(label);
        Label v = new Label(String.format(Locale.US, "Rp %,.0f", amount));
        
        String style = "-fx-font-size: 13px; -fx-text-fill: " + (isBold ? "#1a1c1c;" : "#50443f;");
        if (isBold) style += " -fx-font-weight: bold;";
        
        l.setStyle(style);
        v.setStyle(style);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(l, spacer, v);
        return row;
    }
}
