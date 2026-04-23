package com.example.app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML private Button cifrar;
    @FXML private Button rsa;
    @FXML private Button assinar;
    @FXML private Button gerar;
    @FXML private Button hash;
    @FXML private Button HMACs;
    @FXML private Button verificar;

    private Stage currentStage;

    @FXML private void initialize() {
        cifrar.setOnAction(event -> {
            carregarNovaPagina("cifrassimetricas.fxml", "Cifras simétricas");
        });
        rsa.setOnAction(event -> {
            carregarNovaPagina("rsa.fxml", "Gerar chave RSA");
        });
        assinar.setOnAction(event -> {
            carregarNovaPagina("assinar.fxml", "Calcular assinatura");
        });
        gerar.setOnAction(event -> {
            carregarNovaPagina("gerar-password.fxml", "Gerar palavra-passe");
        });
        hash.setOnAction(event -> {
            carregarNovaPagina("hash.fxml", "Calcular valor de hash");
        });
        HMACs.setOnAction(event -> {
            carregarNovaPagina("hmacs.fxml", "Calcular HMACs");
        });
        verificar.setOnAction(event -> {
            carregarNovaPagina("verificar.fxml", "Verificar assinatura");
        });
    }

    // Método para carregar nova página
    private void carregarNovaPagina(String fxml, String namePage) {
        if (currentStage != null && currentStage.isShowing()) {
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = fxmlLoader.load();
            currentStage = new Stage();
            currentStage.setResizable(false);
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(namePage);
            currentStage.setOnCloseRequest(event -> currentStage = null);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
