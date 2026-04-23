package com.example.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.security.SecureRandom;

public class GerarPasswordController {
    @FXML
    private TextArea passwordTextArea;

    @FXML
    private TextField lengthTextField;

    @FXML
    private Button copyButton;

    @FXML
    protected void onGeneratePasswordButtonClick() {
        // Obter o comprimento da senha do campo de texto
        String lengthText = lengthTextField.getText();

        // Verificar se o campo de texto está vazio
        if (lengthText.isEmpty()) {
            // Exibir mensagem de erro se o campo estiver vazio
            showErrorAlert("Por favor, insira o comprimento da senha.");
            return;
        }

        try {
            // Converter o comprimento da senha para um número inteiro
            int passwordLength = Integer.parseInt(lengthText);

            // Verificar se o comprimento da senha é válido (no mínimo 12 caracteres)
            if (passwordLength < 12) {
                // Exibir mensagem de erro se o comprimento for menor que 12
                showErrorAlert("O comprimento da senha deve ser no mínimo 12.");
                return;
            }

            // Definir os caracteres a serem usados na geração da palavra-passe
            String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
            String numbers = "0123456789";
            String specialCharacters = "!@#$%^&*()-_=+[]{}|;:,.<>?";

            // Combinar todos os caracteres possíveis
            String allCharacters = uppercaseLetters + lowercaseLetters + numbers + specialCharacters;

            // Gerar a palavra-passe aleatória
            StringBuilder password = new StringBuilder();
            SecureRandom random = new SecureRandom();
            for (int i = 0; i < passwordLength; i++) {
                int randomIndex = random.nextInt(allCharacters.length());
                password.append(allCharacters.charAt(randomIndex));
            }

            // Exibir a senha gerada na área de texto
            passwordTextArea.setText(password.toString());

            // Tornar a área de texto e o botão "Copiar" visíveis
            passwordTextArea.setVisible(true);
            passwordTextArea.setManaged(true);
            copyButton.setVisible(true);
            copyButton.setManaged(true);
        } catch (NumberFormatException e) {
            // Exibir mensagem de erro se o comprimento da senha não for um número válido
            showErrorAlert("Por favor, insira um número válido para o comprimento da senha.");
        }
    }

    @FXML
    protected void onCopyPasswordButtonClick() {
        String password = passwordTextArea.getText();

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(password);
        clipboard.setContent(content);

        showInfoAlert("Senha copiada para a área de transferência.");
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
