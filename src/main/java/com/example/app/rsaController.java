package com.example.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class rsaController {

    @FXML
    private TextField filenameTextField;

    @FXML
    private Button generateRSAButton;

    @FXML
    private TextField directoryTextField;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        filenameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Quando ganha foco
                filenameTextField.selectAll();
            }
        });
    }

    @FXML
    protected void onGenerateRSAButtonClick() {
        String filename = filenameTextField.getText();
        String directory = directoryTextField.getText();

        if (filename.isEmpty()) {
            showErrorAlert("Por favor, insira o nome do arquivo de saída.");
            return;
        }

        if (directory.isEmpty()) {
            showErrorAlert("Por favor, selecione um diretório para salvar os arquivos.");
            return;
        }

        generateRSAKeyPair(filename, directory);

        // Limpar o campo de texto após a geração das chaves
        filenameTextField.clear();
    }

    @FXML
    protected void onBrowseButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecionar Diretório");

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            directoryTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void generateRSAKeyPair(String filename, String directory) {
        try {
            // Gerar um par de chaves RSA
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Obter a chave pública e privada do par de chaves
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Salvar a chave privada em um arquivo PEM
            saveKeyToPEM(directory + "/" + filename + "_private.pem", privateKey, "PRIVATE KEY");

            // Salvar a chave pública em um arquivo PEM
            saveKeyToPEM(directory + "/" + filename + "_public.pem", publicKey, "PUBLIC KEY");

            // Exibir uma mensagem de sucesso
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Chave RSA gerada");
            alert.setHeaderText(null);
            alert.setContentText("O par de chaves RSA foi gerado com sucesso e salvo nos arquivos " +
                    filename + "_private.pem e " + filename + "_public.pem.");
            alert.showAndWait();
        } catch (NoSuchAlgorithmException | IOException e) {
            showErrorAlert("Erro ao gerar o par de chaves RSA: " + e.getMessage());
        }
    }

    private void saveKeyToPEM(String filename, Key key, String description) throws IOException {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        String pemKey = "-----BEGIN " + description + "-----\n" +
                encodedKey +
                "\n-----END " + description + "-----\n";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(pemKey);
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
