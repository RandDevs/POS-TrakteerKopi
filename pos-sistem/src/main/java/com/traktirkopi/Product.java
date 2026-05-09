package com.traktirkopi;

public class Product {
    private String name;
    private double price;
    private String category;
    private String imagePath; // Tambahan variabel untuk gambar

    public Product(String name, double price, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getImagePath() { return imagePath; } // Getter gambar
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}