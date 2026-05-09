package com.traktirkopi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrimaryController {
    @FXML private Label tabCoffee;
    @FXML private Label tabNonCoffee;
    @FXML private Label tabSnacks;
    @FXML private Label tabSeasonal;

    @FXML private FlowPane menuFlowPane;
    @FXML private TableView<CartItem> cartTable;
    @FXML private Label totalLabel;

    private ObservableList<CartItem> cartData = FXCollections.observableArrayList();
    private double totalBelanja = 0;
    
    // List untuk menyimpan semua data menu
    private List<Product> allProducts = new ArrayList<>();

    @FXML
    public void initialize() {
        setupTable();
        loadAllProducts();
        
        // Default tampilan saat pertama kali dibuka
        showCategory("Coffee");
        tabCoffee.getStyleClass().add("active-tab"); 
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

        cartTable.setItems(cartData);
    }

    private void loadAllProducts() {
        // ── COFFEE ──────────────────────────────────────────────────────
        allProducts.add(new Product("Espresso",               15000, "Coffee", ""));
        allProducts.add(new Product("Americano",              18000, "Coffee", ""));
        allProducts.add(new Product("Caffe Latte",            22000, "Coffee", ""));
        allProducts.add(new Product("Cappuccino",             24000, "Coffee", ""));
        allProducts.add(new Product("Flat White",             23000, "Coffee", ""));
        allProducts.add(new Product("Kopi Susu Gula Aren",    20000, "Coffee", ""));
        allProducts.add(new Product("Cold Brew",              25000, "Coffee", ""));
        allProducts.add(new Product("Vietnamese Drip",        20000, "Coffee", ""));
        allProducts.add(new Product("Macchiato",              26000, "Coffee", ""));
        allProducts.add(new Product("Mocha",                  26000, "Coffee", ""));
        allProducts.add(new Product("Affogato",               28000, "Coffee", ""));
        allProducts.add(new Product("Iced Coffee Susu",       22000, "Coffee", ""));
        allProducts.add(new Product("Kopi Tubruk",            12000, "Coffee", ""));
        allProducts.add(new Product("Dirty Coffee",           27000, "Coffee", ""));
        allProducts.add(new Product("Caramel Macchiato",      28000, "Coffee", ""));

        // ── NON-COFFEE ──────────────────────────────────────────────────
        allProducts.add(new Product("Matcha Latte",           25000, "Non-Coffee", ""));
        allProducts.add(new Product("Taro Latte",             25000, "Non-Coffee", ""));
        allProducts.add(new Product("Cokelat Panas",          20000, "Non-Coffee", ""));
        allProducts.add(new Product("Iced Chocolate",         22000, "Non-Coffee", ""));
        allProducts.add(new Product("Lemon Tea",              18000, "Non-Coffee", ""));
        allProducts.add(new Product("Lychee Tea",             20000, "Non-Coffee", ""));
        allProducts.add(new Product("Peach Tea",              20000, "Non-Coffee", ""));
        allProducts.add(new Product("Strawberry Smoothie",    28000, "Non-Coffee", ""));
        allProducts.add(new Product("Mango Smoothie",         28000, "Non-Coffee", ""));
        allProducts.add(new Product("Jus Alpukat",            22000, "Non-Coffee", ""));
        allProducts.add(new Product("Jus Jeruk",              18000, "Non-Coffee", ""));
        allProducts.add(new Product("Blue Pea Latte",         26000, "Non-Coffee", ""));
        allProducts.add(new Product("Oat Milk Latte",         28000, "Non-Coffee", ""));
        allProducts.add(new Product("Sparkling Lemon",        22000, "Non-Coffee", ""));
        allProducts.add(new Product("Vanilla Milk",           20000, "Non-Coffee", ""));

        // ── SNACKS ──────────────────────────────────────────────────────
        allProducts.add(new Product("Croissant",              15000, "Snacks", ""));
        allProducts.add(new Product("Roti Bakar Cokelat",     12000, "Snacks", ""));
        allProducts.add(new Product("Roti Bakar Keju",        13000, "Snacks", ""));
        allProducts.add(new Product("Cheese Cake",            28000, "Snacks", ""));
        allProducts.add(new Product("Banana Bread",           18000, "Snacks", ""));
        allProducts.add(new Product("Waffle Original",        22000, "Snacks", ""));
        allProducts.add(new Product("Waffle Matcha",          25000, "Snacks", ""));
        allProducts.add(new Product("Donat Gula",             10000, "Snacks", ""));
        allProducts.add(new Product("Donat Cokelat",          12000, "Snacks", ""));
        allProducts.add(new Product("Muffin Blueberry",       20000, "Snacks", ""));
        allProducts.add(new Product("Muffin Cokelat",         20000, "Snacks", ""));
        allProducts.add(new Product("Sandwich Keju",          25000, "Snacks", ""));
        allProducts.add(new Product("Sandwich Tuna",          27000, "Snacks", ""));
        allProducts.add(new Product("Brownies",               18000, "Snacks", ""));
        allProducts.add(new Product("Cookies Susu",           15000, "Snacks", ""));

        // ── SEASONAL ────────────────────────────────────────────────────
        allProducts.add(new Product("Pumpkin Spice Latte",    32000, "Seasonal", ""));
        allProducts.add(new Product("Strawberry Matcha",      30000, "Seasonal", ""));
        allProducts.add(new Product("Ube Latte",              30000, "Seasonal", ""));
        allProducts.add(new Product("Salted Caramel Mocha",   32000, "Seasonal", ""));
        allProducts.add(new Product("Cherry Blossom Latte",   30000, "Seasonal", ""));
        allProducts.add(new Product("Coconut Cold Brew",      28000, "Seasonal", ""));
        allProducts.add(new Product("Brown Sugar Oat Latte",  30000, "Seasonal", ""));
        allProducts.add(new Product("Lavender Latte",         30000, "Seasonal", ""));
        allProducts.add(new Product("Tiramisu Latte",         32000, "Seasonal", ""));
        allProducts.add(new Product("Rose Lychee Tea",        28000, "Seasonal", ""));
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
    private void bukaPembayaran() throws IOException {
        if (cartData.isEmpty()) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/traktirkopi/payment.fxml"));
        Parent root = loader.load();

        // Pastikan class PaymentController sudah kamu buat ya agar bagian ini tidak error!
        PaymentController payCtrl = loader.getController();
        payCtrl.setTotal(totalBelanja);

        Stage payStage = new Stage();
        payStage.setTitle("Pembayaran - Trakteer Kopi");
        payStage.initModality(Modality.APPLICATION_MODAL);
        payStage.setScene(new Scene(root, 560, 680));
        payStage.setResizable(false);
        payStage.showAndWait();

        cartData.clear();
        hitungTotal();
    }

    private void hitungTotal() {
        totalBelanja = 0;
        for (CartItem item : cartData) {
            totalBelanja += (item.getPrice() * item.getQty());
        }
        totalLabel.setText("Rp " + String.format("%,.0f", totalBelanja));
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
    private void hapusPilihan() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartData.remove(selectedItem);
            hitungTotal();
        }
    }

    @FXML
    private void kosongkanKeranjang() {
        cartData.clear();
        hitungTotal();
    }
}