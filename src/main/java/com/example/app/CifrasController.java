package com.example.app;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class CifrasController {

    @FXML private TextField inputFileField;
    @FXML private TextField outputFileField;
    @FXML private TextField cipherKeyField;
    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private ComboBox<String> modoComboBox;
    @FXML private Label outputLabel;
    private Path inputFilePath; private Path outputFilePath;

    public void initialize() {
        algorithmComboBox.getItems().addAll("AES", "DES", "ARIA");
        modoComboBox.getItems().addAll("ECB", "CBC");
        inputFileField.setOnMouseClicked(event -> inputFilePath = openFileChooser(inputFileField));
        outputFileField.setOnMouseClicked(event -> outputFilePath = openFileChooser(outputFileField));
    }
    @FXML private void handleEncryptButton(ActionEvent event) {
        String cipherKey = cipherKeyField.getText();
        String selectedAlgorithm = algorithmComboBox.getValue();
        String selectedModo = modoComboBox.getValue();

        if (inputFilePath == null || outputFilePath == null || cipherKey.isEmpty() || selectedAlgorithm == null || selectedModo == null) {
            outputLabel.setText("Por favor, preencha todos os campos corretamente.");
            outputLabel.setTextFill(Paint.valueOf("#FF0000"));
            return;
        }

        String algorithm = "";
        String transformation = "";

        switch (selectedAlgorithm) {
            case "AES" -> {
                if (selectedModo.equals("ECB")) {
                    algorithm = "AES";
                    transformation = "AES/ECB/PKCS5Padding";
                }
                if (selectedModo.equals("CBC")) {
                    algorithm = "AES";
                    transformation = "AES/CBC/PKCS5Padding";
                }
            }
            case "DES" -> {
                if (selectedModo.equals("ECB")) {
                    algorithm = "DES";
                    transformation = "DES/ECB/PKCS5Padding";
                }
                if (selectedModo.equals("CBC")) {
                    algorithm = "DES";
                    transformation = "DES/CBC/PKCS5Padding";
                }
            }
            case "ARIA" -> {
                if (selectedModo.equals("ECB")) {
                    algorithm = "ARIA";
                    transformation = "ARIA/ECB/PKCS5Padding";
                }
                if (selectedModo.equals("CBC")) {
                    algorithm = "ARIA";
                    transformation = "ARIA/CBC/PKCS5Padding";
                }
            }
            default -> {
                return;
            }
        }

        // Verifica chave
        if(!isKeyValid(cipherKey, algorithm)){
            return;
        }

        // Cifrar o ficheiro
        try {
            SecretKeySpec secretKey = new SecretKeySpec(cipherKey.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(transformation);

            if (selectedModo.equals("CBC")) {
                byte[] iv = new byte[16];
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            }

            byte[] inputBytes = Files.readAllBytes(inputFilePath);
            byte[] encryptedBytes = cipher.doFinal(inputBytes);

            Files.write(outputFilePath, Base64.getEncoder().encode(encryptedBytes), StandardOpenOption.CREATE);

            outputLabel.setText("Arquivo cifrado com sucesso!");
            outputLabel.setTextFill(Paint.valueOf("#12C43F"));
            inputFileField.setText("");
            outputFileField.setText("");
            cipherKeyField.setText("");
            modoComboBox.setValue(null);
            algorithmComboBox.setValue(null);

        } catch (Exception e) {
            e.printStackTrace();

            outputLabel.setText("Arquivo não cifrado.");
            outputLabel.setTextFill(Paint.valueOf("#FF0000"));
        }
    }

    private boolean isKeyValid(String key, String algorithm) {
        if (!key.matches("[0-9A-Fa-f]+")){
            outputLabel.setText("Chave não é hexadecimal!");
            outputLabel.setTextFill(Paint.valueOf("#FF0000"));
            return false;
        }

        int keyLength = key.length();

        switch (algorithm) {
            case "AES", "ARIA":
                return keyLength == 32;
            case "DES":
                return keyLength == 16;
            default:
                outputLabel.setText("Chave não corresponde a este algoritmo!");
                outputLabel.setTextFill(Paint.valueOf("#FF0000"));
                return false;
        }
    }
    private Path openFileChooser(TextField textField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo");

        File desktop = new File(System.getProperty("user.home"), "Desktop");
        if (desktop.exists() && desktop.isDirectory()) {
            fileChooser.setInitialDirectory(desktop);
        } else {
            File documents = new File(System.getProperty("user.home"), "Documents");
            if (documents.exists() && documents.isDirectory()) {
                fileChooser.setInitialDirectory(documents);
            }
        }

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(textField.getScene().getWindow());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            textField.setText(filePath);
            return file.toPath();
        }
        return null;
    }
}