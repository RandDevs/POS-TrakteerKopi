package com.traktirkopi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private String orderId;
    private LocalDateTime timestamp;
    private String customerName;
    private String paymentMethod;
    private double totalAmount;
    private double subtotal;
    private double tax;
    private List<CartItem> items; // A copy of the cart items

    public Transaction(String orderId, String customerName, String paymentMethod, double subtotal, double tax, double totalAmount, List<CartItem> items) {
        this.orderId = orderId;
        this.timestamp = LocalDateTime.now();
        this.customerName = (customerName == null || customerName.trim().isEmpty()) ? "Walk-in Customer" : customerName;
        this.paymentMethod = paymentMethod;
        this.subtotal = subtotal;
        this.tax = tax;
        this.totalAmount = totalAmount;
        
        // Make a deep copy of items to avoid reference issues when cart is cleared
        this.items = new ArrayList<>();
        for (CartItem item : items) {
            this.items.add(new CartItem(item.getName(), item.getQty(), item.getPrice()));
        }
    }

    public String getOrderId() { return orderId; }
    
    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        return timestamp.format(formatter);
    }
    
    public String getCustomerName() { return customerName; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalAmount() { return totalAmount; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public List<CartItem> getItems() { return items; }
}
