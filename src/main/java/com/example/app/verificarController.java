package com.example.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

import javafx.stage.FileChooser;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class verificarController {

    @FXML
    private TextField inputField;

    @FXML
    private TextField signatureField;

    @FXML
    private TextField publicKeyField;

    @FXML
    private Label infoLabel;

    @FXML
    private Label result;

    @FXML
    private ComboBox algBox;

    @FXML
    public void verifySignature() {
        try {
            byte[] inputData = Files.readAllBytes(Paths.get(inputField.getText()));
            byte[] signatureBytes = readSignature(signatureField.getText());
            PublicKey publicKey = readPublicKey(publicKeyField.getText());

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(inputData);

            boolean verified = signature.verify(signatureBytes);

            if (verified) {
                infoLabel.setText("Assinatura digital válida");
                infoLabel.setTextFill(Paint.valueOf("GREEN"));
            } else {
                infoLabel.setText("Assinatura digital inválida");
                infoLabel.setTextFill(Paint.valueOf("RED"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Erro ao verificar a assinatura digital");
            infoLabel.setTextFill(Paint.valueOf("RED"));
        }
    }

    private byte[] readSignature(String signaturePath) throws Exception {
        return Files.readAllBytes(Paths.get(signaturePath));
    }

    private PublicKey readPublicKey(String publicKeyPath) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKeyPath));
        String keyString = new String(keyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec;

        if (keyString.contains("-----BEGIN PUBLIC KEY-----")) {
            // trata do formato PEM
            try (FileReader keyReader = new FileReader(publicKeyPath);
                 PemReader pemReader = new PemReader(keyReader)) {

                PemObject pemObject = pemReader.readPemObject();
                if (pemObject == null) {
                    throw new IllegalArgumentException("O arquivo PEM não contém um objeto PEM válido.");
                }
                keyBytes = pemObject.getContent();
            }
        }
        // verifica se já está no formato DER, sem os cabeçalhos PEM
        publicKeySpec = new X509EncodedKeySpec(keyBytes);

        return keyFactory.generatePublic(publicKeySpec);
    }

    @FXML
    // caixa de diálogo para escolher o ficheiro de entrada
    public void browseInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo de Entrada");
        File selectedFile = fileChooser.showOpenDialog(inputField.getScene().getWindow());
        if (selectedFile != null) {
            // atualizar a textfield do fxml com o path
            inputField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    // caixa de diálogo para escolher o ficheiro com a chave pública
    public void browsePublicKey() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Chave Pública");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PEM files (*.pem)", "*.pem"));
        File selectedFile = fileChooser.showOpenDialog(publicKeyField.getScene().getWindow());
        if (selectedFile != null) {
            // atualizar a textfield do fxml com o path
            publicKeyField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    // caixa de diálogo para escolher o ficheiro da assinatura
    public void browseSignature() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Assinatura Digital");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary files (*.bin)", "*.bin"));
        File selectedFile = fileChooser.showOpenDialog(signatureField.getScene().getWindow());
        if (selectedFile != null) {
            // atualizar a textfield do fxml com o path
            signatureField.setText(selectedFile.getAbsolutePath());
            // efetuar a verificação da assinatura
            verifySignature();
        }
    }
}
