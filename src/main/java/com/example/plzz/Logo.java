package com.example.plzz;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Logo extends Application {

    @FXML
    private Label slogan;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label loadingLabel;

    private static MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        if (slogan != null) {
            slogan.setOpacity(0);
        }
        if (progressBar != null) {
            progressBar.setProgress(0);
        }
        if (loadingLabel != null) {
            loadingLabel.setText("Loading... 0%");
        }
        progressBar.setStyle("-fx-accent: #000000;");

        applyNeonEffect(progressBar);

        playStartupSound();
    }



    private void applyNeonEffect(ProgressBar progressBar) {
        DropShadow neonGlow = new DropShadow();
        neonGlow.setColor(Color.CYAN);
        neonGlow.setRadius(10);
        neonGlow.setSpread(0.5);
        neonGlow.setOffsetX(0);
        neonGlow.setOffsetY(0);
        progressBar.setEffect(neonGlow);
    }



    public void showSloganWithAnimation() {
        if (slogan != null) {
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), slogan);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), slogan);
            translateTransition.setFromY(30);
            translateTransition.setToY(0);

            fadeTransition.play();
            translateTransition.play();
        }
    }

    public void startProgressBarAnimation() {
        if (progressBar != null && loadingLabel != null) {
            Timeline timeline = new Timeline();
            final int totalSteps = 100;
            final double durationInSeconds = 5.0;

            for (int i = 0; i <= totalSteps; i++) {
                int progressValue = i;
                KeyFrame kf = new KeyFrame(Duration.seconds(i * (durationInSeconds / totalSteps)), event -> {
                    double progress = progressValue / 100.0;
                    progressBar.setProgress(progress);
                    loadingLabel.setText("Loading... " + progressValue + "%");

                    if (progressValue == 100) {
                        stopSound();
                        Stage stage = (Stage) progressBar.getScene().getWindow();
                        openOptionsScreen();
                        stage.close();
                    }
                });
                timeline.getKeyFrames().add(kf);
            }

            timeline.play();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader logoLoader = new FXMLLoader(getClass().getResource("/com/example/plzz/logo.fxml"));
        Parent logoRoot = logoLoader.load();

        Scene logoScene = new Scene(logoRoot);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(logoScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> stopSound());

        Logo logoController = logoLoader.getController();

        // Start progress bar immediately
        logoController.startProgressBarAnimation();

        // Animate slogan after 1s
        PauseTransition sloganDelay = new PauseTransition(Duration.seconds(1));
        sloganDelay.setOnFinished(event -> logoController.showSloganWithAnimation());
        sloganDelay.play();
    }

    private void playStartupSound() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            String path = getClass().getResource("/sounds/plz.mp3").toExternalForm();
            if (path == null) {
                System.out.println("Sound file not found: /sounds/plz.mp3");
                return;
            }

            Media sound = new Media(path);
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error loading sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    private void openOptionsScreen() {
        try {
            FXMLLoader optionsLoader = new FXMLLoader(getClass().getResource("options.fxml"));
            Parent optionsRoot = optionsLoader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Options");
            newStage.setScene(new Scene(optionsRoot));
            newStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
