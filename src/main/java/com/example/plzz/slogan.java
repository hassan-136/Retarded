package com.example.plzz;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class slogan {
    @FXML
    private Label slogan;

    public void showSloganWithAnimation() {
        if (slogan != null) {
            slogan.setOpacity(0); // Start as invisible
            slogan.setVisible(true);

            // Fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), slogan);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        }
    }
}
