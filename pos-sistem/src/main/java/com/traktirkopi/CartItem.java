package com.traktirkopi;

public class CartItem {
    private String name;
    private int qty;
    private double price;

    public CartItem(String name, int qty, double price) {
        this.name = name;
        this.qty = qty;
        this.price = price;
    }

    // Getter wajib ada agar JavaFX TableView bisa membaca datanya
    public String getName() { return name; }
    public int getQty() { return qty; }
    public double getPrice() { return price; }
    
    // Setter untuk menambah jumlah barang jika diklik berkali-kali
    public void addQty(int amount) {
        this.qty += amount;
    }
}