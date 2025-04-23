package com.example.plzz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.io.IOException;

public class Options {

    @FXML
    private Button adminButton;

    @FXML
    private Button voterButton;
    @FXML
    private Button exitButton;

    @FXML
    private void initialize() {
        addHoverEffect(adminButton);
        addHoverEffect(voterButton);
        addHoverEffect(exitButton);
    }
    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void addHoverEffect(Button button) {
        // Scale up on hover
        button.setOnMouseEntered((MouseEvent e) -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        // Scale back on exit
        button.setOnMouseExited((MouseEvent e) -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    @FXML
    private void adminbuttonOnAction() {
        playClickSound();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_login.fxml"));

            if (loader.getLocation() == null) {
                System.out.println("Error: admin_login.fxml not found!");
                return;
            }

            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Admin Login");
            newStage.show();

            // Close current Options window
            Stage currentStage = (Stage) adminButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void VoterbuttonOnAction() {
        playClickSound();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("voter_login.fxml"));

            if (loader.getLocation() == null) {
                System.out.println("Error: voter_login.fxml not found!");
                return;
            }

            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Voter Login");
            newStage.show();

            // Close current Options window
            Stage currentStage = (Stage) voterButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playClickSound() {
        try {
            String path = getClass().getResource("/sounds/click.wav").toExternalForm();
            if (path == null) {
                System.out.println("Click sound file not found!");
                return;
            }

            Media sound = new Media(path);
            MediaPlayer clickPlayer = new MediaPlayer(sound);
            clickPlayer.setVolume(0.7); // Adjust volume (0.0 to 1.0)
            clickPlayer.play();
        } catch (Exception e) {
            System.out.println("Error playing click sound: " + e.getMessage());
        }
    }
}
