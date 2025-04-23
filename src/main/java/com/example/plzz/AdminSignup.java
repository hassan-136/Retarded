package com.example.plzz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AdminSignup {

    @FXML private TextField txtFullName;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    @FXML private ImageView imgProfile;
    @FXML private Button btnUpload;
    @FXML private Button btnSignUp;
    @FXML private Button btnCancel;

    private String imagePath = ""; // Will be set on image upload

    @FXML
    public void initialize() {
        applyHoverEffect(btnSignUp);
        applyHoverEffect(btnCancel);
        applyHoverEffect(btnUpload);
        applyHoverEffect(txtFullName);
        applyHoverEffect(txtUsername);
        applyHoverEffect(txtEmail);
        applyHoverEffect(txtPassword);
        applyHoverEffect(imgProfile);
    }

    private void applyHoverEffect(javafx.scene.Node node) {
        node.setOnMouseEntered(e -> {
            node.setScaleX(1.05);
            node.setScaleY(1.05);
        });
        node.setOnMouseExited(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }

    @FXML
    private void handleUploadImage() {
        Stage stage = (Stage) btnUpload.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                File destDir = new File("admin_pictures");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                File destFile = new File(destDir, txtUsername.getText() + ".png");
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imagePath = destFile.getAbsolutePath();
                imgProfile.setImage(new Image(destFile.toURI().toString()));
            } catch (IOException e) {
                showAlert("Failed to upload image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSignUp() {
        String fullName = txtFullName.getText();
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("All fields are required!");
            return;
        }

        if (!email.contains("@") || !email.endsWith("gmail.com")) {
            showAlert("Please enter a valid email with '@' and 'gmail.com'");
            return;
        }

        if (imagePath.isEmpty()) {
            showAlert("Please upload a profile picture.");
            return;
        }

        if (isEmailOrUsernameTaken(email, username)) {
            return;
        }

        saveAdminData(fullName, username, email, password, imagePath);
        showAlert("Admin successfully signed up!");
        loadLoginPage();
    }

    private boolean isEmailOrUsernameTaken(String email, String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/data/admin.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String existingUsername = parts[1];
                    String existingEmail = parts[2];

                    if (existingUsername.equals(username)) {
                        showAlert("Username already exists. Please choose a different one.");
                        return true;
                    }
                    if (existingEmail.equals(email)) {
                        showAlert("Email already exists. Please choose a different one.");
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        return false;
    }

    private void saveAdminData(String fullName, String username, String email, String password, String imagePath) {
        try {
            File adminFile = new File("resources/data/admin.txt");
            File parentDir = adminFile.getParentFile();
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.out.println("Failed to create directories!");
                    return;
                }
            }

            FileWriter writer = new FileWriter(adminFile, true);
            writer.write(fullName + "," + username + "," + email + "," + password + "," + imagePath + "\n");
            writer.close();

            System.out.println("Admin data saved to: " + adminFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving admin data:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        loadLoginPage();
    }

    private void loadLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Failed to load login page: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
