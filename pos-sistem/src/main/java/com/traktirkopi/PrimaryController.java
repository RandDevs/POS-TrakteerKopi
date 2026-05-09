package com.traktirkopi;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PrimaryController {
    @FXML private Label tabCoffee;
    @FXML private Label tabNonCoffee;
    @FXML private Label tabSnacks;
    @FXML private Label tabSeasonal;

    @FXML private FlowPane menuFlowPane;
    @FXML private TableView<CartItem> cartTable;
    @FXML private Label totalLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private TextField customerNameField;
    @FXML private Button btnQris;
    @FXML private Button btnCash;

    // Sidebar navigation
    @FXML private BorderPane centerPane;
    @FXML private HBox posTopHeader;
    @FXML private ScrollPane catalogScroll;
    @FXML private VBox cartSidebar;
    @FXML private Button btnSidebarPOS;
    @FXML private Button btnSidebarBackoffice;

    private ObservableList<CartItem> cartData = FXCollections.observableArrayList();
    private double totalBelanja = 0;
    private String selectedPaymentMethod = "Cash"; // Default payment method
    
    // List untuk menyimpan semua data menu
    private List<Product> allProducts = new ArrayList<>();

    // Saved POS view references for restoration
    private Node savedPosTop;
    private Node savedPosCenter;
    private Node savedPosRight;

    @FXML
    public void initialize() {
        setupTable();
        loadAllProducts();
        setupPaymentButtons();
        
        // Default tampilan saat pertama kali dibuka
        showCategory("Coffee");
        tabCoffee.getStyleClass().add("active-tab");

        // Save POS view nodes for later restoration
        savedPosTop = centerPane.getTop();
        savedPosCenter = centerPane.getCenter();
        savedPosRight = centerPane.getRight();
    }

    @FXML
    private void switchTab(javafx.scene.input.MouseEvent event) {
        // 1. Reset semua tab ke tampilan default (hilangkan garis bawah)
        tabCoffee.getStyleClass().remove("active-tab");
        tabNonCoffee.getStyleClass().remove("active-tab");
        tabSnacks.getStyleClass().remove("active-tab");
        tabSeasonal.getStyleClass().remove("active-tab");

        // 2. Ambil Label mana yang baru saja diklik oleh kasir
        Label clickedTab = (Label) event.getSource();

        // 3. Tambahkan class active-tab (garis bawah) ke Label yang diklik
        if (!clickedTab.getStyleClass().contains("active-tab")) {
            clickedTab.getStyleClass().add("active-tab");
        }
        
        // 4. PERBAIKAN: Ambil nama kategori dari teks dan filter menunya!
        String selectedCategory = clickedTab.getText();
        System.out.println("Memuat kategori: " + selectedCategory);
        showCategory(selectedCategory);
    }

    private void setupTable() {
        TableColumn<CartItem, String> colName = (TableColumn<CartItem, String>) cartTable.getColumns().get(0);
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<CartItem, Integer> colQty = (TableColumn<CartItem, Integer>) cartTable.getColumns().get(1);
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        TableColumn<CartItem, Double> colPrice = (TableColumn<CartItem, Double>) cartTable.getColumns().get(2);
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Format kolom harga sebagai mata uang IDR (Rp 30,000)
        colPrice.setCellFactory(column -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "Rp %,.0f", price));
                }
            }
        });

        // Kolom hapus: tombol ✕ per baris untuk menghapus item
        TableColumn<CartItem, Void> colDelete = new TableColumn<>("");
        colDelete.setPrefWidth(40);
        colDelete.setSortable(false);
        colDelete.setResizable(false);
        colDelete.setCellFactory(column -> new TableCell<CartItem, Void>() {
            private final Button btnDelete = new Button("\u2716");
            {
                btnDelete.getStyleClass().add("btn-row-delete");
                btnDelete.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartData.remove(item);
                    hitungTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        cartTable.getColumns().add(colDelete);

        cartTable.setItems(cartData);
    }

    // ── Payment Method State Logic ──────────────────────────────────────
    private void setupPaymentButtons() {
        // Default: Cash is selected
        selectPaymentMethod("Cash");

        btnCash.setOnAction(e -> selectPaymentMethod("Cash"));
        btnQris.setOnAction(e -> selectPaymentMethod("QRIS"));
    }

    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;

        // Toggle active class
        btnQris.getStyleClass().remove("btn-payment-active");
        btnCash.getStyleClass().remove("btn-payment-active");

        if ("QRIS".equals(method)) {
            btnQris.getStyleClass().add("btn-payment-active");
        } else {
            btnCash.getStyleClass().add("btn-payment-active");
        }
    }

    private void loadAllProducts() {
        // Flaticon CDN icon URLs per category for varied product images
        // Coffee icons
        String iconEspresso     = "https://cdn-icons-png.flaticon.com/128/924/924514.png";
        String iconAmericano    = "https://cdn-icons-png.flaticon.com/128/3127/3127399.png";
        String iconLatte        = "https://cdn-icons-png.flaticon.com/128/590/590836.png";
        String iconCappuccino   = "https://cdn-icons-png.flaticon.com/128/924/924530.png";
        String iconCoffee       = "https://cdn-icons-png.flaticon.com/128/1047/1047503.png";
        String iconColdBrew     = "https://cdn-icons-png.flaticon.com/128/2935/2935409.png";
        String iconMocha        = "https://cdn-icons-png.flaticon.com/128/3076/3076186.png";
        String iconIcedCoffee   = "https://cdn-icons-png.flaticon.com/128/2405/2405691.png";

        // Non-Coffee icons
        String iconTea          = "https://cdn-icons-png.flaticon.com/128/2935/2935316.png";
        String iconMatcha       = "https://cdn-icons-png.flaticon.com/128/6397/6397285.png";
        String iconChocolate    = "https://cdn-icons-png.flaticon.com/128/3076/3076072.png";
        String iconSmoothie     = "https://cdn-icons-png.flaticon.com/128/2405/2405479.png";
        String iconJuice        = "https://cdn-icons-png.flaticon.com/128/3050/3050131.png";
        String iconMilk         = "https://cdn-icons-png.flaticon.com/128/3050/3050158.png";

        // Snack icons
        String iconCroissant    = "https://cdn-icons-png.flaticon.com/128/3081/3081967.png";
        String iconBread        = "https://cdn-icons-png.flaticon.com/128/3081/3081903.png";
        String iconCake         = "https://cdn-icons-png.flaticon.com/128/3081/3081889.png";
        String iconWaffle       = "https://cdn-icons-png.flaticon.com/128/3081/3081984.png";
        String iconDonut        = "https://cdn-icons-png.flaticon.com/128/3081/3081940.png";
        String iconMuffin       = "https://cdn-icons-png.flaticon.com/128/3081/3081924.png";
        String iconSandwich     = "https://cdn-icons-png.flaticon.com/128/3081/3081952.png";
        String iconCookies      = "https://cdn-icons-png.flaticon.com/128/3081/3081906.png";

        // Seasonal icons
        String iconPumpkin      = "https://cdn-icons-png.flaticon.com/128/1728/1728777.png";
        String iconStrawberry   = "https://cdn-icons-png.flaticon.com/128/590/590772.png";
        String iconFlower       = "https://cdn-icons-png.flaticon.com/128/2672/2672505.png";
        String iconCoconut      = "https://cdn-icons-png.flaticon.com/128/3050/3050124.png";
        String iconLavender     = "https://cdn-icons-png.flaticon.com/128/2672/2672558.png";
        String iconStar         = "https://cdn-icons-png.flaticon.com/128/1828/1828884.png";

        // ── COFFEE ──────────────────────────────────────────────────────
        allProducts.add(new Product("Espresso",               15000, "Coffee", iconEspresso));
        allProducts.add(new Product("Americano",              18000, "Coffee", iconAmericano));
        allProducts.add(new Product("Caffe Latte",            22000, "Coffee", iconLatte));
        allProducts.add(new Product("Cappuccino",             24000, "Coffee", iconCappuccino));
        allProducts.add(new Product("Flat White",             23000, "Coffee", iconLatte));
        allProducts.add(new Product("Kopi Susu Gula Aren",    20000, "Coffee", iconCoffee));
        allProducts.add(new Product("Cold Brew",              25000, "Coffee", iconColdBrew));
        allProducts.add(new Product("Vietnamese Drip",        20000, "Coffee", iconCoffee));
        allProducts.add(new Product("Macchiato",              26000, "Coffee", iconCappuccino));
        allProducts.add(new Product("Mocha",                  26000, "Coffee", iconMocha));
        allProducts.add(new Product("Affogato",               28000, "Coffee", iconMocha));
        allProducts.add(new Product("Iced Coffee Susu",       22000, "Coffee", iconIcedCoffee));
        allProducts.add(new Product("Kopi Tubruk",            12000, "Coffee", iconEspresso));
        allProducts.add(new Product("Dirty Coffee",           27000, "Coffee", iconAmericano));
        allProducts.add(new Product("Caramel Macchiato",      28000, "Coffee", iconColdBrew));

        // ── NON-COFFEE ──────────────────────────────────────────────────
        allProducts.add(new Product("Matcha Latte",           25000, "Non-Coffee", iconMatcha));
        allProducts.add(new Product("Taro Latte",             25000, "Non-Coffee", iconSmoothie));
        allProducts.add(new Product("Cokelat Panas",          20000, "Non-Coffee", iconChocolate));
        allProducts.add(new Product("Iced Chocolate",         22000, "Non-Coffee", iconChocolate));
        allProducts.add(new Product("Lemon Tea",              18000, "Non-Coffee", iconTea));
        allProducts.add(new Product("Lychee Tea",             20000, "Non-Coffee", iconTea));
        allProducts.add(new Product("Peach Tea",              20000, "Non-Coffee", iconTea));
        allProducts.add(new Product("Strawberry Smoothie",    28000, "Non-Coffee", iconSmoothie));
        allProducts.add(new Product("Mango Smoothie",         28000, "Non-Coffee", iconSmoothie));
        allProducts.add(new Product("Jus Alpukat",            22000, "Non-Coffee", iconJuice));
        allProducts.add(new Product("Jus Jeruk",              18000, "Non-Coffee", iconJuice));
        allProducts.add(new Product("Blue Pea Latte",         26000, "Non-Coffee", iconMatcha));
        allProducts.add(new Product("Oat Milk Latte",         28000, "Non-Coffee", iconMilk));
        allProducts.add(new Product("Sparkling Lemon",        22000, "Non-Coffee", iconJuice));
        allProducts.add(new Product("Vanilla Milk",           20000, "Non-Coffee", iconMilk));

        // ── SNACKS ──────────────────────────────────────────────────────
        allProducts.add(new Product("Croissant",              15000, "Snacks", iconCroissant));
        allProducts.add(new Product("Roti Bakar Cokelat",     12000, "Snacks", iconBread));
        allProducts.add(new Product("Roti Bakar Keju",        13000, "Snacks", iconBread));
        allProducts.add(new Product("Cheese Cake",            28000, "Snacks", iconCake));
        allProducts.add(new Product("Banana Bread",           18000, "Snacks", iconBread));
        allProducts.add(new Product("Waffle Original",        22000, "Snacks", iconWaffle));
        allProducts.add(new Product("Waffle Matcha",          25000, "Snacks", iconWaffle));
        allProducts.add(new Product("Donat Gula",             10000, "Snacks", iconDonut));
        allProducts.add(new Product("Donat Cokelat",          12000, "Snacks", iconDonut));
        allProducts.add(new Product("Muffin Blueberry",       20000, "Snacks", iconMuffin));
        allProducts.add(new Product("Muffin Cokelat",         20000, "Snacks", iconMuffin));
        allProducts.add(new Product("Sandwich Keju",          25000, "Snacks", iconSandwich));
        allProducts.add(new Product("Sandwich Tuna",          27000, "Snacks", iconSandwich));
        allProducts.add(new Product("Brownies",               18000, "Snacks", iconCake));
        allProducts.add(new Product("Cookies Susu",           15000, "Snacks", iconCookies));

        // ── SEASONAL ────────────────────────────────────────────────────
        allProducts.add(new Product("Pumpkin Spice Latte",    32000, "Seasonal", iconPumpkin));
        allProducts.add(new Product("Strawberry Matcha",      30000, "Seasonal", iconStrawberry));
        allProducts.add(new Product("Ube Latte",              30000, "Seasonal", iconStar));
        allProducts.add(new Product("Salted Caramel Mocha",   32000, "Seasonal", iconMocha));
        allProducts.add(new Product("Cherry Blossom Latte",   30000, "Seasonal", iconFlower));
        allProducts.add(new Product("Coconut Cold Brew",      28000, "Seasonal", iconCoconut));
        allProducts.add(new Product("Brown Sugar Oat Latte",  30000, "Seasonal", iconStar));
        allProducts.add(new Product("Lavender Latte",         30000, "Seasonal", iconLavender));
        allProducts.add(new Product("Tiramisu Latte",         32000, "Seasonal", iconCake));
        allProducts.add(new Product("Rose Lychee Tea",        28000, "Seasonal", iconFlower));
    }

    private void showCategory(String category) {
        menuFlowPane.getChildren().clear();
        for (Product p : allProducts) {
            if (p.getCategory().equals(category)) {
                menuFlowPane.getChildren().add(createProductCard(p));
            }
        }
    }

    private VBox createProductCard(Product p) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setSpacing(12);
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setPrefWidth(180); 
        card.setMinHeight(220);

        VBox imageBox = new VBox();
        imageBox.getStyleClass().add("product-icon-box");
        imageBox.setPrefHeight(120);
        imageBox.setMaxWidth(Double.MAX_VALUE);
        imageBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        ImageView imageView = new ImageView();
        try {
            // PERBAIKAN: Berikan gambar default jika URL kosong
            String imagePath = p.getImagePath();
            if (imagePath == null || imagePath.trim().isEmpty()) {
                imagePath = "https://cdn-icons-png.flaticon.com/128/1047/1047503.png"; // Ikon kopi default
            }
            
            Image img = new Image(imagePath, true); 
            imageView.setImage(img);
            imageView.setFitWidth(60); 
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);
        } catch (Exception ex) {
            System.out.println("Gagal memuat gambar untuk: " + p.getName());
        }
        imageBox.getChildren().add(imageView); 

        Label nameLabel = new Label(p.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");
        nameLabel.setWrapText(true); 
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label priceLabel = new Label("Rp " + String.format("%,.0f", p.getPrice()));
        priceLabel.getStyleClass().add("price-text");

        Button btnAdd = new Button("+ Tambah");
        btnAdd.getStyleClass().add("btn-add");
        btnAdd.setMaxWidth(Double.MAX_VALUE); 
        btnAdd.setPadding(new javafx.geometry.Insets(8, 0, 8, 0));
        
        btnAdd.setOnAction(e -> addToCart(p));

        card.getChildren().addAll(imageBox, nameLabel, priceLabel, btnAdd);

        // Smooth hover animation using ScaleTransition
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        card.setOnMouseEntered(e -> {
            scaleOut.stop();
            scaleIn.playFromStart();
        });
        card.setOnMouseExited(e -> {
            scaleIn.stop();
            scaleOut.playFromStart();
        });

        return card;
    }

    private String getCategoryEmoji(String category) {
        return switch (category) {
            case "Coffee" -> "☕";
            case "Non-Coffee" -> "🧋";
            case "Snacks" -> "🥐";
            case "Seasonal" -> "🌸";
            default -> "🍽";
        };
    }
    
    // ── BAYAR: Buka halaman pembayaran sebagai popup ──────────────────
    @FXML
    private void bukaPembayaran() {
        if (cartData.isEmpty()) {
            return;
        }

        if ("QRIS".equals(selectedPaymentMethod)) {
            showQrisModal();
        } else {
            showCashModal();
        }
    }

    // ── QRIS Modal ─────────────────────────────────────────────────────
    private void showQrisModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.setTitle("QRIS Payment");

        // Root container with shadow + border
        HBox root = new HBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        // ── Left side: Order Summary ──
        VBox leftPane = new VBox(12);
        leftPane.setPrefWidth(280);

        Label titleLabel = new Label("Order Summary");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

        // Scrollable items list
        VBox itemsList = new VBox(8);
        itemsList.setPadding(new Insets(4, 0, 4, 0));
        for (CartItem item : cartData) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(item.getName() + " x" + item.getQty());
            nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #50443f;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label priceLabel = new Label(String.format(Locale.US, "Rp %,.0f", item.getPrice() * item.getQty()));
            priceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a1c1c; -fx-font-weight: bold;");
            row.getChildren().addAll(nameLabel, spacer, priceLabel);
            itemsList.getChildren().add(row);
        }

        javafx.scene.control.ScrollPane itemsScroll = new javafx.scene.control.ScrollPane(itemsList);
        itemsScroll.setFitToWidth(true);
        itemsScroll.setPrefHeight(250);
        itemsScroll.setMaxHeight(250);
        itemsScroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        itemsScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(itemsScroll, Priority.ALWAYS);

        Separator sep = new Separator();

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setPadding(new Insets(4, 0, 0, 0));
        Label totalTextLabel = new Label("Total");
        totalTextLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");
        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);
        Label totalValueLabel = new Label(String.format(Locale.US, "Rp %,.0f", totalBelanja));
        totalValueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #715547;");
        totalRow.getChildren().addAll(totalTextLabel, totalSpacer, totalValueLabel);

        leftPane.getChildren().addAll(titleLabel, itemsScroll, sep, totalRow);

        // ── Right side: QR Code ──
        VBox rightPane = new VBox(20);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPrefWidth(240);

        VBox qrBox = new VBox();
        qrBox.setAlignment(Pos.CENTER);
        qrBox.setPrefSize(180, 180);
        qrBox.setMaxSize(180, 180);
        qrBox.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        ImageView qrImage = new ImageView();
        try {
            qrImage.setImage(new Image("https://cdn-icons-png.flaticon.com/128/3220/3220181.png", true));
            qrImage.setFitWidth(120);
            qrImage.setFitHeight(120);
            qrImage.setPreserveRatio(true);
        } catch (Exception ignored) {}
        qrBox.getChildren().add(qrImage);

        Label scanLabel = new Label("Scan to Pay");
        scanLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #715547;");

        Label instructionLabel = new Label("Open your e-wallet app and\nscan this QR code");
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #82746f;");
        instructionLabel.setTextAlignment(TextAlignment.CENTER);

        Button btnDone = new Button("\u2713  Done \u2014 Payment Received");
        btnDone.setMaxWidth(Double.MAX_VALUE);
        btnDone.setStyle("-fx-background-color: #536346; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 14; -fx-cursor: hand;");
        btnDone.setOnAction(e -> {
            modal.close();
            cartData.clear();
            hitungTotal();
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8;");
        btnCancel.setOnAction(e -> modal.close());

        rightPane.getChildren().addAll(qrBox, scanLabel, instructionLabel, btnDone, btnCancel);

        root.getChildren().addAll(leftPane, rightPane);

        Scene scene = new Scene(root, 650, 500);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // ── Cash Modal ─────────────────────────────────────────────────────
    private void showCashModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.setTitle("Cash Payment");

        VBox root = new VBox(16);
        root.setPadding(new Insets(36, 40, 40, 40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #fdfcfb; -fx-background-radius: 16; -fx-border-color: #d3c3bc; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        // Title
        Label titleLabel = new Label("\ud83d\udcb5  Cash Payment");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a1c1c;");

        // Total Due
        VBox totalBox = new VBox(4);
        totalBox.setAlignment(Pos.CENTER);
        totalBox.setPadding(new Insets(16));
        totalBox.setStyle("-fx-background-color: #f7f3f0; -fx-background-radius: 12;");
        Label totalDueLabel = new Label("Total Tagihan");
        totalDueLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #82746f;");
        Label totalAmountLabel = new Label(String.format(Locale.US, "Rp %,.0f", totalBelanja));
        totalAmountLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #715547;");
        totalBox.getChildren().addAll(totalDueLabel, totalAmountLabel);

        // Cash Input
        Label inputLabel = new Label("Uang Diterima");
        inputLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #82746f; -fx-padding: 4 0 0 0;");

        TextField cashInput = new TextField();
        cashInput.setPromptText("Masukkan jumlah...");
        cashInput.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12 16;");
        cashInput.setMaxWidth(Double.MAX_VALUE);

        // Quick Action Buttons (FlowPane for wrapping)
        FlowPane quickButtons = new FlowPane();
        quickButtons.setHgap(8);
        quickButtons.setVgap(8);
        quickButtons.setAlignment(Pos.CENTER);

        Button btnExact = createQuickButton("Uang Pas");
        btnExact.setOnAction(e -> cashInput.setText(String.format("%.0f", totalBelanja)));

        Button btn20k = createQuickButton("Rp 20,000");
        btn20k.setOnAction(e -> cashInput.setText("20000"));

        Button btn50k = createQuickButton("Rp 50,000");
        btn50k.setOnAction(e -> cashInput.setText("50000"));

        Button btn100k = createQuickButton("Rp 100,000");
        btn100k.setOnAction(e -> cashInput.setText("100000"));

        Button btn200k = createQuickButton("Rp 200,000");
        btn200k.setOnAction(e -> cashInput.setText("200000"));

        quickButtons.getChildren().addAll(btnExact, btn20k, btn50k, btn100k, btn200k);

        // Change Display
        VBox changeBox = new VBox(4);
        changeBox.setAlignment(Pos.CENTER);
        changeBox.setPadding(new Insets(16));
        changeBox.setStyle("-fx-background-color: #f7f3f0; -fx-background-radius: 12;");
        Label changeLabel = new Label("Kembalian");
        changeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #82746f;");
        Label changeValueLabel = new Label("Rp 0");
        changeValueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #536346;");
        changeBox.getChildren().addAll(changeLabel, changeValueLabel);

        // Real-time change calculation
        cashInput.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                String cleaned = newVal.replaceAll("[^0-9]", "");
                if (cleaned.isEmpty()) {
                    changeValueLabel.setText("Rp 0");
                    changeValueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #536346;");
                    return;
                }
                double paid = Double.parseDouble(cleaned);
                double change = paid - totalBelanja;
                if (change >= 0) {
                    changeValueLabel.setText(String.format(Locale.US, "Rp %,.0f", change));
                    changeValueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #536346;");
                } else {
                    changeValueLabel.setText(String.format(Locale.US, "- Rp %,.0f", Math.abs(change)));
                    changeValueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #b3261e;");
                }
            } catch (NumberFormatException ex) {
                changeValueLabel.setText("Rp 0");
            }
        });

        // Pay Button
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #b3261e;");

        Button btnPay = new Button("\ud83d\udcb5  Bayar");
        btnPay.setMaxWidth(Double.MAX_VALUE);
        btnPay.setStyle("-fx-background-color: #536346; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 14; -fx-cursor: hand;");
        btnPay.setOnAction(e -> {
            try {
                String cleaned = cashInput.getText().replaceAll("[^0-9]", "");
                if (cleaned.isEmpty()) {
                    errorLabel.setText("\u26a0 Masukkan jumlah uang terlebih dahulu.");
                    return;
                }
                double paid = Double.parseDouble(cleaned);
                if (paid < totalBelanja) {
                    errorLabel.setText(String.format(Locale.US, "\u26a0 Uang kurang! Kurang Rp %,.0f", totalBelanja - paid));
                    return;
                }
                modal.close();
                cartData.clear();
                hitungTotal();
            } catch (NumberFormatException ex) {
                errorLabel.setText("\u26a0 Input tidak valid.");
            }
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: #82746f; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8;");
        btnCancel.setOnAction(e -> modal.close());

        root.getChildren().addAll(titleLabel, totalBox, inputLabel, cashInput, quickButtons, changeBox, errorLabel, btnPay, btnCancel);

        Scene scene = new Scene(root, 440, 680);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private Button createQuickButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #d3c3bc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 12px; -fx-text-fill: #715547; -fx-font-weight: bold; -fx-cursor: hand;");
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void hitungTotal() {
        double subtotal = 0;
        for (CartItem item : cartData) {
            subtotal += (item.getPrice() * item.getQty());
        }
        double tax = subtotal * 0.11;
        totalBelanja = subtotal + tax;

        subtotalLabel.setText(String.format(Locale.US, "Rp %,.0f", subtotal));
        taxLabel.setText(String.format(Locale.US, "Rp %,.0f", tax));
        totalLabel.setText(String.format(Locale.US, "Rp %,.0f", totalBelanja));
    }

    private void addToCart(Product p) {
        boolean itemExists = false;
        for (CartItem item : cartData) {
            if (item.getName().equals(p.getName())) {
                item.addQty(1);
                itemExists = true;
                cartTable.refresh();
                break;
            }
        }
        if (!itemExists) {
            cartData.add(new CartItem(p.getName(), 1, p.getPrice()));
        }
        hitungTotal();
    }

    @FXML
    private void kosongkanKeranjang() {
        if (cartData.isEmpty()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Order");
        alert.setHeaderText("Clear entire order?");
        alert.setContentText("Are you sure you want to clear the entire current order?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartData.clear();
            hitungTotal();
        }
    }

    // ── Navigation Logic ────────────────────────────────────────────────
    @FXML
    private void showPosView() {
        // Toggle active button style
        btnSidebarBackoffice.getStyleClass().remove("sidebar-btn-active");
        btnSidebarBackoffice.getStyleClass().add("sidebar-btn");
        
        btnSidebarPOS.getStyleClass().remove("sidebar-btn");
        btnSidebarPOS.getStyleClass().add("sidebar-btn-active");

        // Restore POS view
        centerPane.setTop(savedPosTop);
        centerPane.setCenter(savedPosCenter);
        centerPane.setRight(savedPosRight);
    }

    @FXML
    private void showBackofficeView() {
        // Toggle active button style
        btnSidebarPOS.getStyleClass().remove("sidebar-btn-active");
        btnSidebarPOS.getStyleClass().add("sidebar-btn");
        
        btnSidebarBackoffice.getStyleClass().remove("sidebar-btn");
        btnSidebarBackoffice.getStyleClass().add("sidebar-btn-active");

        try {
            // Load Backoffice view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/traktirkopi/backoffice.fxml"));
            Parent backofficeView = loader.load();

            // Hide POS specific elements from BorderPane
            centerPane.setTop(null);
            centerPane.setRight(null);
            
            // Set center to backoffice
            centerPane.setCenter(backofficeView);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load Back Office");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Logout of POS?");
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                App.setRoot("login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}