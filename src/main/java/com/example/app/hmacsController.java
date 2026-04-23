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
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class hmacsController {

    @FXML
    private TextField fileNameField;  // Campo de texto para o nome do arquivo

    @FXML
    private TextField keyField;  // Campo de texto para a chave hexadecimal

    @FXML
    private ComboBox<String> algorithmComboBox;  // ComboBox para selecionar o algoritmo HMAC

    @FXML
    private Label resultLabel;  // Rótulo para mostrar o resultado

    @FXML
    private Button calculateButton;  // Botão para calcular o HMAC

    @FXML
    private void initialize() {
        calculateButton.setOnAction(e -> calculateHMAC());

        // Configuração do evento de clique no campo de nome do arquivo para abrir o seletor de arquivos
        fileNameField.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                openFileChooser();
            }
        });

        // Adiciona opções de algoritmo ao ComboBox e define um valor padrão
        algorithmComboBox.setItems(FXCollections.observableArrayList("HmacSHA256", "HmacSHA1", "HmacMD5", "HmacSHA512"));
        algorithmComboBox.setValue("HmacSHA256");
    }

    private void openFileChooser() {
        // Configura o seletor de arquivos para escolher um arquivo do desktop
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo");
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        fileChooser.setInitialDirectory(desktop);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(fileNameField.getScene().getWindow());
        if (file != null) {
            fileNameField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void calculateHMAC() {
        // Método responsável por calcular o HMAC do arquivo especificado pelo usuário

        // Obtem os valores dos campos de entrada para o nome do arquivo, algoritmo selecionado e chave hexadecimal
        String fileName = fileNameField.getText();
        String algorithm = algorithmComboBox.getValue();
        String keyHex = keyField.getText();

        // Verifica se a chave fornecida é um hexadecimal válido
        if (!isValidHex(keyHex)) {
            // Configura a mensagem de erro no rótulo de resultado se a chave não for válida
            resultLabel.setText("Chave inválida. Use apenas caracteres hexadecimais e em número par.");
            return; // Interrompe a execução se a chave não for válida
        }

        try {
            // Cria um objeto File para o arquivo especificado
            File file = new File(fileName);
            // Converte a chave hexadecimal em um array de bytes
            byte[] keyBytes = hexStringToByteArray(keyHex);
            // Cria um objeto SecretKeySpec que associa a chave ao algoritmo de HMAC escolhido
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);

            // Obtem uma instância de Mac para o algoritmo especificado e inicializa com a chave
            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);

            // Prepara um FileInputStream para ler o arquivo especificado
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024]; // Buffer para leitura do arquivo
            int bytesRead;
            // Lê o arquivo em blocos de 1024 bytes e alimenta o Mac com esses dados
            while ((bytesRead = fis.read(buffer)) != -1) {
                mac.update(buffer, 0, bytesRead);
            }
            // Completa o cálculo do HMAC
            byte[] macBytes = mac.doFinal();
            // Converte o resultado do HMAC em uma string hexadecimal
            String result = bytesToHex(macBytes);

            // Define o texto do rótulo de resultado com o HMAC calculado
            resultLabel.setText("HMAC: " + result);

            // Fecha o FileInputStream
            fis.close();
        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            // Captura exceções relacionadas a algoritmos não encontrados, chaves inválidas e erros de I/O
            e.printStackTrace(); // Imprime a pilha de erros no console
            // Define o texto do rótulo de resultado para mostrar a mensagem de erro
            resultLabel.setText("Erro ao calcular HMAC: " + e.getMessage());
        }
    }

    private boolean isValidHex(String s) {
        // Verifica se a string é um hexadecimal válido
        return s.matches("[0-9A-Fa-f]+") && (s.length() % 2 == 0);
    }

    private byte[] hexStringToByteArray(String s) {
        // Converte uma string hexadecimal em um array de bytes
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private String bytesToHex(byte[] bytes) {
        // Converte um array de bytes em uma string hexadecimal
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
