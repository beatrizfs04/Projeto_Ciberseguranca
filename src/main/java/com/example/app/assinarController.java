package com.example.app;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Objects;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class assinarController {

    @FXML
    private TextField inputField;

    @FXML
    private TextField privateKeyField;

    @FXML
    private TextField outputField;

    @FXML
    private Label infoLabel;

    @FXML
    public void calculateSignature() {
        try {
            // verificar se os campos obrigatórios estão vazios
            if (Objects.equals(inputField.getText(), "") || Objects.equals(privateKeyField.getText(), "") || Objects.equals(outputField.getText(), "")) {
                // mostra mensagem se algum deles está vazio
                infoLabel.setText("Os campos não podem estar em branco");
                infoLabel.setTextFill(Paint.valueOf("RED"));
                return;
            }

            // ler o ficheiro de entrada
            byte[] data = Files.readAllBytes(Paths.get(inputField.getText()));

            // ler a chave privada
            PrivateKey privateKey = readPrivateKey(privateKeyField.getText());

            // inicializar o Signature (java.security)
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);

            // calcular a assinatura digital
            byte[] digitalSignature = signature.sign();

            // guardar a assinatura calculada no ficheiro de saída
            try (FileOutputStream fos = new FileOutputStream(outputField.getText())) {
                fos.write(digitalSignature);
            }

            // mensagem de sucesso
            System.out.println("Guardado em " + outputField.getText());
            infoLabel.setText("Calculada com sucesso e guardada no lugar especificado");
            infoLabel.setTextFill(Paint.valueOf("GREEN"));

        } catch (Exception e) {
            // lançar uma exception
            System.out.println("Erro ao calcular a assinatura digital");
            e.printStackTrace();
            infoLabel.setText("Erro ao calcular a assinatura digital");
            infoLabel.setTextFill(Paint.valueOf("RED"));
        }
    }

    // método para ler o .pem da chave privada
    private PrivateKey readPrivateKey(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
        String keyString = new String(keyBytes);

        if (keyString.contains("-----BEGIN PRIVATE KEY-----")) {
            // trata do formato PEM
            try (FileReader keyReader = new FileReader(filename);
                 PemReader pemReader = new PemReader(keyReader)) {

                PemObject pemObject = pemReader.readPemObject();
                if (pemObject == null) {
                    throw new IllegalArgumentException("O arquivo PEM não contém um objeto PEM válido.");
                }
                keyBytes = pemObject.getContent();
            }
        }

        // encarregar-se da chave privada
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // gerar a chave privada a partir da especificação de chave
        return keyFactory.generatePrivate(keySpec);
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
    // caixa de diálogo para escolher o ficheiro com a chave privada
    public void browsePrivateKeyFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Chave Privada");
        // filtro de extensão para mostrar apenas arquivos PEM
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PEM files", "*.pem"));
        File selectedFile = fileChooser.showOpenDialog(privateKeyField.getScene().getWindow());
        if (selectedFile != null) {
            // atualizar a textfield do fxml com o path
            privateKeyField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    // caixa de diálogo para escolher o ficheiro de saída
    public void browseOutputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Assinatura Digital");
        // filtro de extensão para salvar com a extensão .bin por padrão
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BIN files", "*.bin"));
        File selectedFile = fileChooser.showSaveDialog(outputField.getScene().getWindow());
        if (selectedFile != null) {
            // adicionar a extensão .bin se não estiver presente
            if (!selectedFile.getPath().endsWith(".bin")) {
                selectedFile = new File(selectedFile.getPath() + ".bin");
            }
            // atualizar a textfield do fxml com o path
            outputField.setText(selectedFile.getAbsolutePath());
            // calcular a assinatura digital assim que o ficheiro de saída é escolhido
            calculateSignature();
        }
    }
}
