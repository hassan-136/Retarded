package com.example.plzz;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VoterLogin {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField cnicField;

    @FXML
    private TextField captchaField;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Label sloganLabel;

    @FXML
    private Label captchaLabel; // Added Label for CAPTCHA

    private String generatedCaptcha; // To hold the generated CAPTCHA

    private final String sloganText = "Your Voice, Your Power!";
    private int sloganIndex = 0;
    private Timeline typewriter;

    public void initialize() {
        startSloganTypewriterAnimation();
        generateCaptcha(); // Generate CAPTCHA when the screen loads

        loginButton.setOnAction(e -> handleLogin());
        backButton.setOnAction(this::setBackButton);
    }

    // Method to generate a new CAPTCHA number
    private void generateCaptcha() {
        int captchaNumber = (int) (Math.random() * 9000) + 1000; // Generates a 4-digit number
        generatedCaptcha = String.valueOf(captchaNumber); // Convert to String
        captchaLabel.setText(generatedCaptcha); // Display the CAPTCHA
    }

    // Start the typewriter animation for the slogan
    private void startSloganTypewriterAnimation() {
        typewriter = new Timeline();
        typewriter.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(150), event -> {
            if (sloganIndex > sloganText.length()) {
                sloganLabel.setText("");
                sloganIndex = 0;
            } else {
                sloganLabel.setText(sloganText.substring(0, sloganIndex));
                sloganIndex++;
            }
        });

        typewriter.getKeyFrames().add(keyFrame);
        typewriter.play();
    }

    // Handle login logic and CAPTCHA verification
    private void handleLogin() {
        String name = fullNameField.getText();
        String cnic = cnicField.getText();
        String captcha = captchaField.getText();

        if (name.isEmpty() || cnic.isEmpty() || captcha.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all fields.");
        } else if (!captcha.equals(generatedCaptcha)) { // Validate against the generated CAPTCHA
            showAlert(Alert.AlertType.ERROR, "Captcha Error", "Wrong captcha. Try again.");
            generateCaptcha(); // Generate a new CAPTCHA on failure
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + name + "!");
            // You can load the next screen or reset fields here
        }
    }

    // Handle the back button action
    private void setBackButton(ActionEvent event) {
        try {
            FXMLLoader optionsLoader = new FXMLLoader(getClass().getResource("options.fxml"));
            Parent optionsRoot = optionsLoader.load();

            // Create and show the new stage
            Stage newStage = new Stage();
            newStage.setTitle("Options");
            newStage.setScene(new Scene(optionsRoot));
            newStage.show();

            // Close the current window
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Method to show alert messages
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
