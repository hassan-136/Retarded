package com.example.plzz;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class AdminLogin {

    @FXML
    private AnchorPane adminLoginPane;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button SignUpButton;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), adminLoginPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        applyHoverEffect(loginButton);
        applyHoverEffect(SignUpButton);
        applyHoverEffect(usernameField);
        applyHoverEffect(passwordField);
        applyHoverEffect(backButton);
    }

    private void applyHoverEffect(javafx.scene.Node node) {
        node.setOnMouseEntered(e -> {
            node.setScaleX(1.1);
            node.setScaleY(1.1);
        });

        node.setOnMouseExited(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }

    @FXML
    private void onLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (verifyAdmin(username, password)) {
            System.out.println("Login successful");
            loadAdminDashboard();
        } else {
            showAlert("Login Failed", "Invalid username or password", Alert.AlertType.ERROR);
        }
    }

    private boolean verifyAdmin(String username, String password) {
        File adminFile = new File("resources/data/admin.txt");

        if (!adminFile.exists()) {
            showAlert("Error", "Admin data file not found!", Alert.AlertType.ERROR);
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(adminFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[1].equals(username) && parts[3].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadSignupPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/plzz/admin_signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) SignUpButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_dashboard.fxml"));
            Parent root = loader.load();

            String username = usernameField.getText();
            String password = passwordField.getText();

            AdminDashboard controller = loader.getController();
            controller.setLoggedInCredentials(username, password);
            controller.loadAdminInfo();

            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Admin Dashboard");
            newStage.show();

            // Close current stage after 3 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();
            });
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onActionButton() {
        loadSignupPage();
    }

    @FXML
    private void onBackButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));

            if (loader.getLocation() == null) {
                System.out.println("Error: options.fxml not found!");
                return;
            }

            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Options");
            newStage.show();

            // Close current stage after 3 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                Stage currentStage = (Stage) backButton.getScene().getWindow();
                currentStage.close();
            });
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
