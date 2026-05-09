package com.traktirkopi;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;

public class OrdersController {

    @FXML private TableView<Transaction> ordersTable;
    @FXML private TextField searchField;

    private FilteredList<Transaction> filteredItems;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        // Order ID
        TableColumn<Transaction, String> colId = (TableColumn<Transaction, String>) ordersTable.getColumns().get(0);
        colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colId.setCellFactory(col -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(id);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #536346;");
                }
            }
        });

        // Time
        TableColumn<Transaction, String> colTime = (TableColumn<Transaction, String>) ordersTable.getColumns().get(1);
        colTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));

        // Customer
        TableColumn<Transaction, String> colCust = (TableColumn<Transaction, String>) ordersTable.getColumns().get(2);
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCust.setCellFactory(col -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(name);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        // Payment Method
        TableColumn<Transaction, String> colPay = (TableColumn<Transaction, String>) ordersTable.getColumns().get(3);
        colPay.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        // Total Amount
        TableColumn<Transaction, Double> colTotal = (TableColumn<Transaction, Double>) ordersTable.getColumns().get(4);
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTotal.setCellFactory(col -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "Rp %,.0f", total));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #715547;");
                }
            }
        });

        // Actions
        TableColumn<Transaction, Void> colActions = (TableColumn<Transaction, Void>) ordersTable.getColumns().get(5);
        colActions.setSortable(false);
        colActions.setCellFactory(col -> new TableCell<Transaction, Void>() {
            private final Button btnView = new Button("📄 View Detail");
            {
                btnView.setStyle("-fx-background-color: #f7f3f0; -fx-text-fill: #715547; -fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 6 12; -fx-cursor: hand;");
                
                btnView.setOnAction(e -> {
                    Transaction item = getTableView().getItems().get(getIndex());
                    showReceiptModal(item);
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btnView);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        filteredItems = new FilteredList<>(DataStore.transactions, p -> true);
        ordersTable.setItems(filteredItems);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredItems.setPredicate(tx -> {
                if (filter.isEmpty()) return true;
                return tx.getOrderId().toLowerCase().contains(filter)
                    || tx.getCustomerName().toLowerCase().contains(filter);
            });
        });
    }

    private void showReceiptModal(Transaction tx) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(0);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        // Styled to look like a paper receipt
        root.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        // Receipt Header
        Label storeLabel = new Label("TRAKTEER KOPI");
        storeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");
        Label subtitleLabel = new Label("Premium coffee, brewed with love.");
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #82746f; -fx-padding: 0 0 15 0;");

        // Meta Info
        VBox metaBox = new VBox(4);
        metaBox.setStyle("-fx-border-color: #e2e2e2; -fx-border-width: 1 0 1 0; -fx-padding: 10 0;");
        metaBox.getChildren().addAll(
            createReceiptRow("Order ID", tx.getOrderId(), false),
            createReceiptRow("Date", tx.getFormattedTime(), false),
            createReceiptRow("Customer", tx.getCustomerName(), false),
            createReceiptRow("Payment", tx.getPaymentMethod(), false)
        );

        // Items List
        VBox itemsBox = new VBox(6);
        itemsBox.setPadding(new Insets(15, 0, 15, 0));
        for (CartItem ci : tx.getItems()) {
            Label nameLabel = new Label(ci.getName());
            nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");
            
            HBox qtyPriceRow = new HBox();
            Label qtyLabel = new Label(ci.getQty() + "x");
            qtyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #82746f;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label priceLabel = new Label(String.format(Locale.US, "Rp %,.0f", (ci.getPrice() * ci.getQty())));
            priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1a1c1c;");
            
            qtyPriceRow.getChildren().addAll(qtyLabel, spacer, priceLabel);
            
            VBox itemRow = new VBox(2, nameLabel, qtyPriceRow);
            itemsBox.getChildren().add(itemRow);
        }

        ScrollPane scrollItems = new ScrollPane(itemsBox);
        scrollItems.setFitToWidth(true);
        scrollItems.setPrefHeight(200);
        scrollItems.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        // Totals
        VBox totalsBox = new VBox(6);
        totalsBox.setStyle("-fx-border-color: #e2e2e2; -fx-border-width: 1 0 0 0; -fx-padding: 15 0;");
        totalsBox.getChildren().addAll(
            createReceiptRow("Subtotal", String.format(Locale.US, "Rp %,.0f", tx.getSubtotal()), false),
            createReceiptRow("Tax (11%)", String.format(Locale.US, "Rp %,.0f", tx.getTax()), false),
            createReceiptRow("Total", String.format(Locale.US, "Rp %,.0f", tx.getTotalAmount()), true)
        );

        // Footer / Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button btnClose = new Button("Close");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");
        btnClose.setOnAction(ev -> modal.close());

        Button btnReprint = new Button("🖨 Reprint");
        btnReprint.setStyle("-fx-background-color: #f7f3f0; -fx-text-fill: #715547; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 16;");
        btnReprint.setOnAction(ev -> {
            System.out.println("Printing Receipt: " + tx.getOrderId());
        });

        buttonBox.getChildren().addAll(btnClose, btnReprint);

        root.getChildren().addAll(storeLabel, subtitleLabel, metaBox, scrollItems, totalsBox, buttonBox);

        Scene scene = new Scene(root, 340, 550);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private HBox createReceiptRow(String leftStr, String rightStr, boolean isBold) {
        HBox row = new HBox();
        Label left = new Label(leftStr);
        Label right = new Label(rightStr);
        
        String style = "-fx-font-size: 12px; -fx-text-fill: " + (isBold ? "#1a1c1c;" : "#82746f;");
        if (isBold) {
            style += " -fx-font-weight: bold; -fx-font-size: 14px;";
        }
        
        left.setStyle(style);
        right.setStyle(style);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(left, spacer, right);
        return row;
    }
}
