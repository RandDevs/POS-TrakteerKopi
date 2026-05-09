package com.traktirkopi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Locale;
import java.util.Optional;

public class BackofficeController {

    @FXML private TableView<Product> menuTable;
    @FXML private TextField searchField;

    private ObservableList<Product> allMenuItems = FXCollections.observableArrayList();
    private FilteredList<Product> filteredItems;

    @FXML
    public void initialize() {
        loadDummyData();
        setupTable();
        setupSearch();
    }

    private void loadDummyData() {
        String ic = "https://cdn-icons-png.flaticon.com/128/924/924514.png";
        String it = "https://cdn-icons-png.flaticon.com/128/2935/2935316.png";
        String is = "https://cdn-icons-png.flaticon.com/128/3081/3081967.png";
        String im = "https://cdn-icons-png.flaticon.com/128/6397/6397285.png";
        String ip = "https://cdn-icons-png.flaticon.com/128/1728/1728777.png";

        allMenuItems.addAll(
            new Product("Espresso",            15000, "Coffee",     ic),
            new Product("Americano",           18000, "Coffee",     ic),
            new Product("Caffe Latte",         22000, "Coffee",     ic),
            new Product("Cappuccino",          24000, "Coffee",     ic),
            new Product("Kopi Susu Gula Aren", 20000, "Coffee",     ic),
            new Product("Cold Brew",           25000, "Coffee",     ic),
            new Product("Matcha Latte",        25000, "Non-Coffee", im),
            new Product("Lemon Tea",           18000, "Non-Coffee", it),
            new Product("Iced Chocolate",      22000, "Non-Coffee", it),
            new Product("Croissant",           15000, "Snacks",     is),
            new Product("Cheese Cake",         28000, "Snacks",     is),
            new Product("Waffle Original",     22000, "Snacks",     is),
            new Product("Pumpkin Spice Latte", 32000, "Seasonal",   ip),
            new Product("Lavender Latte",      30000, "Seasonal",   ip)
        );
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        // Column 0: Image thumbnail
        TableColumn<Product, String> colImage = (TableColumn<Product, String>) menuTable.getColumns().get(0);
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colImage.setSortable(false);
        colImage.setCellFactory(col -> new TableCell<Product, String>() {
            private final ImageView iv = new ImageView();
            {
                iv.setFitWidth(36);
                iv.setFitHeight(36);
                iv.setPreserveRatio(true);
                iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 1);");
            }
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        iv.setImage(new Image(url, true));
                    } catch (Exception ignored) {}
                    setGraphic(iv);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Column 1: Name
        TableColumn<Product, String> colName = (TableColumn<Product, String>) menuTable.getColumns().get(1);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setCellFactory(col -> new TableCell<Product, String>() {
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

        // Column 2: Category
        TableColumn<Product, String> colCategory = (TableColumn<Product, String>) menuTable.getColumns().get(2);
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setCellFactory(col -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String cat, boolean empty) {
                super.updateItem(cat, empty);
                if (empty || cat == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(cat);
                    badge.setStyle("-fx-background-color: #f7f3f0; -fx-text-fill: #715547; -fx-padding: 4 12; -fx-background-radius: 20; -fx-font-size: 11px; -fx-font-weight: bold;");
                    setGraphic(badge);
                }
            }
        });

        // Column 3: Price
        TableColumn<Product, Double> colPrice = (TableColumn<Product, Double>) menuTable.getColumns().get(3);
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(col -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "Rp %,.0f", price));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #715547;");
                }
            }
        });

        // Column 4: Actions (Edit + Delete)
        TableColumn<Product, Void> colActions = (TableColumn<Product, Void>) menuTable.getColumns().get(4);
        colActions.setSortable(false);
        colActions.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("✖");
            private final HBox box = new HBox(8, btnEdit, btnDelete);
            {
                btnEdit.setStyle("-fx-background-color: #f7f3f0; -fx-text-fill: #715547; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 4 10; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #b3261e; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-cursor: hand;");
                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    Product item = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Edit Menu");
                    alert.setHeaderText("Edit: " + item.getName());
                    alert.setContentText("Edit form will be implemented with database integration.");
                    alert.showAndWait();
                });

                btnDelete.setOnAction(e -> {
                    Product item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Menu");
                    confirm.setHeaderText("Delete \"" + item.getName() + "\"?");
                    confirm.setContentText("This action cannot be undone.");
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        allMenuItems.remove(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        filteredItems = new FilteredList<>(allMenuItems, p -> true);
        menuTable.setItems(filteredItems);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredItems.setPredicate(product -> {
                if (filter.isEmpty()) return true;
                return product.getName().toLowerCase().contains(filter)
                    || product.getCategory().toLowerCase().contains(filter);
            });
        });
    }

    @FXML
    private void addNewMenu() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add New Menu");
        alert.setHeaderText("Add New Menu Item");
        alert.setContentText("The add menu form will be implemented with database integration.");
        alert.showAndWait();
    }
}
