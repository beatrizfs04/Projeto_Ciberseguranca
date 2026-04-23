package com.example.app;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hashController {

    @FXML
    private TextField nomeText;

    @FXML
    private Button calcularButton;

    @FXML
    private Label resultadoLabel;

    @FXML
    private ComboBox<String> algorithmComboBox;

    @FXML
    private Path filePath;

    @FXML
    private byte[] hash;

    @FXML
    private String checksum;

    @FXML
    public void initialize() {
        algorithmComboBox.setItems(FXCollections.observableArrayList("SHA-1", "MD5", "SHA-256", "SHA512"));
        nomeText.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                openFileChooser();
            }
        });
    }

    private boolean verificarDados(String nome, String selectedAlgorithm) {
        if (nome.isEmpty() || selectedAlgorithm == null) {
            resultadoLabel.setText(nome.isEmpty() ? "Digite o nome do ficheiro." : "Selecione um algoritmo primeiro.");
            return false;
        }

        filePath = Path.of(nome);
        if (!Files.exists(filePath)) {
            resultadoLabel.setText("Ficheiro não encontrado.");
            return false;
        }

        return true;
    }

    private void openFileChooser() {
        // Configura o seletor de arquivos para escolher um arquivo do desktop
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo");
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        fileChooser.setInitialDirectory(desktop);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(nomeText.getScene().getWindow());
        if (file != null) {
            nomeText.setText(file.getAbsolutePath());
        }
    }

    private void calcularHash(String selectedAlgorithm) {
        try {
            MessageDigest msgDgst = MessageDigest.getInstance(selectedAlgorithm);
            byte[] fileBytes = Files.readAllBytes(filePath);

            msgDgst.update(fileBytes);
            hash = msgDgst.digest();
            checksum = new BigInteger(1, hash).toString(16);
            resultadoLabel.setText("Resultado: " + checksum);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            resultadoLabel.setText("Erro ao calcular hash.");
        }
    }

    @FXML
    public void calcularHash() {
        String nome = nomeText.getText().trim();
        String selectedAlgorithm = algorithmComboBox.getValue();

        if (verificarDados(nome, selectedAlgorithm)) {
            calcularHash(selectedAlgorithm);
        }
    }
}
