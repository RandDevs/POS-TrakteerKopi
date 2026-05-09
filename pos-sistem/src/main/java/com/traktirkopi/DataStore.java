package com.traktirkopi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataStore {
    // Shared in-memory list of transactions accessible across controllers
    public static final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    
    private static int orderCounter = 1001;

    public static String generateOrderId() {
        return "#TRX-" + (orderCounter++);
    }
    
    // Shift State Management
    public static boolean isShiftOpen = false;
    public static double startingCash = 0;
    public static String shiftStartTime = null;
}
