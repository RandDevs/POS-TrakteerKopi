package com.traktirkopi;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.IOException;

public class PaymentController {

    @FXML private Label labelTotal;
    @FXML private Label labelMetode;
    @FXML private Label labelInfo;
    @FXML private Label labelKembalian;
    @FXML private Label labelKembalianValue;

    @FXML private VBox boxCash;
    @FXML private VBox boxQris;
    @FXML private VBox boxKembalian;

    @FXML private TextField inputCash;
    @FXML private Button btnBayarCash;
    @FXML private Button btnBayarQris;

    private double totalBelanja = 0;

    public void setTotal(double total) {
        this.totalBelanja = total;
        labelTotal.setText("Rp " + String.format("%,.0f", total));
    }

    @FXML
    public void initialize() {
        boxCash.setVisible(false);
        boxCash.setManaged(false);
        boxQris.setVisible(false);
        boxQris.setManaged(false);
        boxKembalian.setVisible(false);
        boxKembalian.setManaged(false);
    }

    @FXML
    public void pilihCash() {
        labelMetode.setText("💵  Pembayaran Cash");
        boxCash.setVisible(true);
        boxCash.setManaged(true);
        boxQris.setVisible(false);
        boxQris.setManaged(false);
        boxKembalian.setVisible(false);
        boxKembalian.setManaged(false);
        inputCash.clear();
        inputCash.requestFocus();
    }

    @FXML
    public void pilihQris() {
        labelMetode.setText("📱  Pembayaran QRIS");
        boxQris.setVisible(true);
        boxQris.setManaged(true);
        boxCash.setVisible(false);
        boxCash.setManaged(false);
        boxKembalian.setVisible(false);
        boxKembalian.setManaged(false);
    }

    @FXML
    private void prosesPaymentCash() {
        String inputText = inputCash.getText().trim();
        if (inputText.isEmpty()) {
            labelInfo.setText("⚠ Masukkan jumlah uang terlebih dahulu.");
            labelInfo.setStyle("-fx-text-fill: #e53935; -fx-font-size: 12px;");
            return;
        }

        try {
            double uangDibayar = Double.parseDouble(inputText.replaceAll("[^0-9]", ""));
            if (uangDibayar < totalBelanja) {
                labelInfo.setText("⚠ Uang kurang! Kurang Rp " +
                    String.format("%,.0f", (totalBelanja - uangDibayar)));
                labelInfo.setStyle("-fx-text-fill: #e53935; -fx-font-size: 12px;");
                return;
            }

            double kembalian = uangDibayar - totalBelanja;
            labelKembalianValue.setText("Rp " + String.format("%,.0f", kembalian));
            boxKembalian.setVisible(true);
            boxKembalian.setManaged(true);
            btnBayarCash.setDisable(true);

            labelInfo.setText("✅ Pembayaran berhasil! Kembali ke kasir dalam 3 detik...");
            labelInfo.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 13px; -fx-font-weight: bold;");

            // Otomatis tutup setelah 3 detik
            tutupOtomatis();

        } catch (NumberFormatException e) {
            labelInfo.setText("⚠ Input tidak valid. Masukkan angka saja.");
            labelInfo.setStyle("-fx-text-fill: #e53935; -fx-font-size: 12px;");
        }
    }

    @FXML
    private void prosesPaymentQris() {
        btnBayarQris.setDisable(true);

        labelInfo.setText("✅ Pembayaran QRIS berhasil! Kembali ke kasir dalam 3 detik...");
        labelInfo.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Otomatis tutup setelah 3 detik
        tutupOtomatis();
    }

    // Tutup popup otomatis setelah 3 detik → kembali ke kasir
    private void tutupOtomatis() {
        PauseTransition jeda = new PauseTransition(Duration.seconds(3));
        jeda.setOnFinished(event -> {
            Stage stage = (Stage) labelInfo.getScene().getWindow();
            stage.close();
        });
        jeda.play();
    }

    @FXML
    private void kembaliKeKasir() throws IOException {
        Stage stage = (Stage) labelInfo.getScene().getWindow();
        stage.close();
    }
}