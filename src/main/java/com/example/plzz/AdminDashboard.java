package com.example.plzz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboard implements Initializable {

    @FXML
    private Button viewResultsButton;

    @FXML
    private Button manageCandidatesButton;

    @FXML
    private Button manageVotersButton;

    @FXML
    private Button manageElectionsButton;
    @FXML
    private Button homeButton;

    @FXML
    private ImageView adminImageView;

    @FXML
    private Label adminNameLabel;

    private String loggedInUsername;
    private String loggedInPassword;

    private Parent adminDashboardRoot;

    @FXML
    private void handleHomeButton() {
        try {
            // Load options.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("options.fxml"));
            Parent root = loader.load();

            // Create new stage for options
            Stage newStage = new Stage();
            newStage.setTitle("Options");
            newStage.setScene(new Scene(root));
            newStage.show();

            // Close the current window
            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Setter method to receive credentials from login screen
    public void setLoggedInCredentials(String username, String password) {
        this.loggedInUsername = username;
        this.loggedInPassword = password;
    }

    // Method to load admin info for the logged-in user only
    public void loadAdminInfo() {
        String filePath = "resources/data/admin.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                // Make sure the line has enough fields
                if (parts.length >= 5) {
                    String name = parts[0];
                    String username = parts[1];
                    String password = parts[3];
                    String imagePath = parts[4];

                    // Only display info if username and password match
                    if (username.equals(loggedInUsername) && password.equals(loggedInPassword)) {
                        adminNameLabel.setText(name);

                        if (!imagePath.isEmpty()) {
                            Image adminImage = new Image("file:" + imagePath);
                            adminImageView.setImage(adminImage);
                        } else {
                            adminImageView.setImage(null);
                        }
                        break; // Found the correct user, stop reading
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialize method to load admin info after setting credentials
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scaleButtons(viewResultsButton);
        scaleButtons(manageCandidatesButton);
        scaleButtons(manageVotersButton);
        scaleButtons(manageElectionsButton);
        scaleButtons(homeButton);
        // Avoid calling loadAdminInfo here unless credentials are set
    }

    // Setter method to store the root of the current dashboard
    public void setAdminDashboardRoot(Parent root) {
        this.adminDashboardRoot = root;
    }

    @FXML
    private void handleViewResults(ActionEvent event) {
        System.out.println("Viewing results...");
    }

    @FXML
    private void handleManageVoters(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manage_voters.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Manage Voters");
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageCandidates(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manage_candidates.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Manage Candidates");
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageElections(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("manage_elections.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Manage Elections");
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scaleButtons(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

}
