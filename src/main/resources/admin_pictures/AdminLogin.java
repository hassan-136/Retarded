package admin_pictures;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AdminLogin {

    @FXML
    private AnchorPane adminLoginPane; // Main pane for transition effect

    @FXML
    private TextField usernameField; // Admin username input field

    @FXML
    private PasswordField passwordField; // Admin password input field

    @FXML
    private Button loginButton; // Login button

    @FXML
    private Button SignUpButton; // Sign-up button

    @FXML
    public void initialize() {
        // Apply fade-in effect to the Admin Login screen
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), adminLoginPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    @FXML
    private void onLoginButtonAction() {
        // Get the entered username and password
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Verify the admin credentials
        if (verifyAdmin(username, password)) {
            // If credentials are correct, load the admin dashboard or next screen
            System.out.println("Login successful");
            // You can load the admin dashboard page here
            loadAdminDashboard();
        } else {
            // If credentials are incorrect, show an error message (can be a dialog or label)
            System.out.println("Invalid username or password");
            showAlert("Invalid username or password. Please try again.");
        }
    }

    // Method to verify admin credentials from the "data/admin.txt" file
    private boolean verifyAdmin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/admin.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[1].equals(username) && parts[3].equals(password)) {
                    return true; // Found a match
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // No match found
    }

    // Method to load the Sign-Up page
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

    // Method to load the Admin Dashboard after successful login
    private void loadAdminDashboard() {
        try {
            // Replace this with the code to load your Admin Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/plzz/admin_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle Sign-Up button click (load sign-up page)
    @FXML
    private void onActionButton() {
        loadSignupPage();
    }

    // Helper method to show an alert for login errors
    private void showAlert(String message) {
        // Show an alert with a custom message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
