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

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
                btnEdit.setStyle("-fx-background-color: #f7f3f0; -fx-text-fill: #715547; -fx-font-size: 18px; -fx-background-radius: 8; -fx-padding: 6 12; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #b3261e; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-cursor: hand;");
                box.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    Product item = getTableView().getItems().get(getIndex());
                    showProductModal(item);
                });

                btnDelete.setOnAction(e -> {
                    Product item = getTableView().getItems().get(getIndex());
                    
                    Stage modal = new Stage();
                    modal.initModality(Modality.APPLICATION_MODAL);
                    modal.initStyle(StageStyle.UNDECORATED);

                    VBox root = new VBox(20);
                    root.setPadding(new Insets(30, 40, 30, 40));
                    root.setAlignment(Pos.CENTER);
                    root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

                    Label titleLabel = new Label("Delete Product");
                    titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

                    Label msgLabel = new Label("Are you sure you want to delete " + item.getName() + "?");
                    msgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #50443f;");
                    msgLabel.setWrapText(true);

                    HBox buttonBox = new HBox(12);
                    buttonBox.setAlignment(Pos.CENTER);
                    buttonBox.setPadding(new Insets(10, 0, 0, 0));

                    Button btnCancel = new Button("Cancel");
                    btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
                    btnCancel.setOnAction(ev -> modal.close());

                    Button btnConfirmDelete = new Button("Delete");
                    btnConfirmDelete.setStyle("-fx-background-color: #b3261e; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
                    btnConfirmDelete.setOnAction(ev -> {
                        allMenuItems.remove(item);
                        modal.close();
                    });

                    buttonBox.getChildren().addAll(btnCancel, btnConfirmDelete);
                    root.getChildren().addAll(titleLabel, msgLabel, buttonBox);

                    Scene scene = new Scene(root, 360, 220);
                    scene.setFill(null);
                    modal.setScene(scene);
                    modal.showAndWait();
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
        showProductModal(null);
    }

    private void showProductModal(Product productToEdit) {
        boolean isEditMode = (productToEdit != null);
        String titleText = isEditMode ? "Edit Product" : "Add New Product";

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.setTitle(titleText);

        VBox root = new VBox(16);
        root.setPadding(new Insets(30, 40, 40, 40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        Label titleLabel = new Label(titleText);
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

        // Form Fields
        VBox formBox = new VBox(12);
        
        Label nameLabel = new Label("Product Name");
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #50443f;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter product name");
        nameField.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
        if (isEditMode) nameField.setText(productToEdit.getName());

        Label catLabel = new Label("Category");
        catLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #50443f;");
        ComboBox<String> categoryCombo = new ComboBox<>(FXCollections.observableArrayList("Coffee", "Non-Coffee", "Snacks", "Seasonal"));
        categoryCombo.setPromptText("Select category");
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        categoryCombo.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 4;");
        if (isEditMode) categoryCombo.setValue(productToEdit.getCategory());

        Label priceLabel = new Label("Price (Rp)");
        priceLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #50443f;");
        TextField priceField = new TextField();
        priceField.setPromptText("Enter price (e.g. 25000)");
        priceField.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
        if (isEditMode) priceField.setText(String.format(Locale.US, "%.0f", productToEdit.getPrice()));

        Label imageLabel = new Label("Image URL");
        imageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #50443f;");
        TextField imageField = new TextField();
        imageField.setPromptText("Enter image URL");
        imageField.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
        if (isEditMode) imageField.setText(productToEdit.getImagePath());

        formBox.getChildren().addAll(nameLabel, nameField, catLabel, categoryCombo, priceLabel, priceField, imageLabel, imageField);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #b3261e; -fx-font-size: 12px;");

        // Action Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(16, 0, 0, 0));

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 16;");
        btnCancel.setOnAction(e -> modal.close());

        Button btnSave = new Button("Save Product");
        btnSave.setStyle("-fx-background-color: #536346; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
        btnSave.setOnAction(e -> {
            String name = nameField.getText().trim();
            String cat = categoryCombo.getValue();
            String priceStr = priceField.getText().trim();
            String imgUrl = imageField.getText().trim();

            if (name.isEmpty() || cat == null || priceStr.isEmpty()) {
                errorLabel.setText("⚠ Please fill out Name, Category, and Price.");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
            } catch (NumberFormatException ex) {
                errorLabel.setText("⚠ Invalid price format.");
                return;
            }

            if (imgUrl.isEmpty()) {
                // Default placeholder
                imgUrl = "https://cdn-icons-png.flaticon.com/128/924/924514.png";
            }

            if (isEditMode) {
                productToEdit.setName(name);
                productToEdit.setCategory(cat);
                productToEdit.setPrice(price);
                productToEdit.setImagePath(imgUrl);
                menuTable.refresh();
            } else {
                Product newProduct = new Product(name, price, cat, imgUrl);
                allMenuItems.add(0, newProduct);
            }

            modal.close();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().addAll(spacer, btnCancel, btnSave);

        root.getChildren().addAll(titleLabel, formBox, errorLabel, buttonBox);

        Scene scene = new Scene(root, 400, 560);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }
}
