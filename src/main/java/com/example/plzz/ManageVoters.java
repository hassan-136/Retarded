package com.example.plzz;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ManageVoters {

    @FXML
    private TextField voterNameField;

    @FXML
    private TextField voterCnicField;

    @FXML
    private TextField voterFamilyNumberField;

    @FXML
    private ImageView voterImageView;

    @FXML
    private ListView<HBox> voterListView;

    @FXML
    private Button backButton;
    @FXML
    private Button loadVotersButton;
    @FXML
    private Button addVoterButton;
    @FXML
    private Button uploadButton;

    private File selectedImageFile;
    private String loggedInUsername;
    private String loggedInPassword;

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) voterNameField.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            voterImageView.setImage(new Image("file:" + selectedImageFile.getAbsolutePath()));
        }
    }

    @FXML
    private void handleAddVoter() {
        String name = voterNameField.getText();
        String cnic = voterCnicField.getText();
        String familyNumber = voterFamilyNumberField.getText();

        if (!isValidCnic(cnic)) {
            showAlert(Alert.AlertType.ERROR, "Invalid CNIC", "CNIC must be exactly 13 digits.");
            return;
        }

        if (name.isEmpty() || cnic.isEmpty() || familyNumber.isEmpty() || selectedImageFile == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill all fields and upload an image.");
            return;
        }

        if (isDuplicateCnic(cnic)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate CNIC", "A voter with this CNIC already exists.");
            return;
        }

        File imageDir = new File("resources/voter_images");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        File destFile = new File(imageDir, cnic + "_" + selectedImageFile.getName());
        try {
            Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Image Error", "Could not save image.");
            return;
        }

        try (FileWriter writer = new FileWriter("resources/data/voters.txt", true)) {
            writer.write(name + "," + cnic + "," + familyNumber + "," + destFile.getPath() + "\n");
            showAlert(Alert.AlertType.INFORMATION, "Voter Added", "Voter added successfully!");
            playSuccessSound();
            clearFields();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDuplicateCnic(String cnic) {
        File file = new File("resources/data/voters.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(cnic)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidCnic(String cnic) {
        return cnic != null && cnic.length() == 13 && cnic.matches("[0-9]+");
    }

    public void setLoggedInCredentials(String username, String password) {
        this.loggedInUsername = username;
        this.loggedInPassword = password;
    }

    @FXML
    private void loadVoters() {
        if (voterListView.isVisible()) {
            voterListView.setVisible(false);
        } else {
            voterListView.setVisible(true);
            voterListView.getItems().clear();
            File file = new File("resources/data/voters.txt");
            if (!file.exists()) {
                System.out.println("voters.txt not found.");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String name = parts[0];
                        String cnic = parts[1];
                        String familyNumber = parts[2];
                        String imagePath = parts[3];

                        ImageView imageView = new ImageView();
                        try {
                            Image image = new Image("file:" + new File(imagePath).getAbsolutePath().replace("\\", "/"));
                            imageView.setImage(image);
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            imageView.setPreserveRatio(true);
                        } catch (Exception e) {
                            System.out.println("Image not loaded: " + imagePath);
                            continue;
                        }

                        String voterInfo = "Name: " + name + "\nCNIC: " + cnic + "\nFamily#: " + familyNumber;
                        javafx.scene.control.Label label = new javafx.scene.control.Label(voterInfo);
                        label.setStyle("-fx-font-size: 13px; -fx-text-fill: black;");
                        label.setWrapText(true);

                        Button deleteButton = new Button("Delete");
                        deleteButton.setOnAction(e -> handleDeleteVoter(cnic));
                        applyScaleEffect(deleteButton); // 🔁 Add scale effect to each Delete button

                        HBox hBox = new HBox(10);
                        hBox.getChildren().addAll(imageView, label, deleteButton);

                        voterListView.getItems().add(hBox);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleDeleteVoter(String cnic) {
        File file = new File("resources/data/voters.txt");
        StringBuilder updatedContent = new StringBuilder();
        boolean voterDeleted = false;
        String imagePathToDelete = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[1].equals(cnic)) {
                    voterDeleted = true;
                    if (parts.length >= 4) {
                        imagePathToDelete = parts[3];
                    }
                    continue;
                }
                updatedContent.append(line).append("\n");
            }

            if (voterDeleted) {
                if (imagePathToDelete != null) {
                    File imageFile = new File(imagePathToDelete.trim());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(updatedContent.toString());
                }

                loadVoters();
                showAlert(Alert.AlertType.INFORMATION, "Voter Deleted", "Voter has been deleted successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Voter Not Found", "No voter found with the given CNIC.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoadVotersButton() {
        loadVoters();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        voterNameField.clear();
        voterCnicField.clear();
        voterFamilyNumberField.clear();
        String defaultImagePath = "file:D:/plzz/resources/images/pf.png";
        voterImageView.setImage(new Image(defaultImagePath));
        selectedImageFile = null;
    }

    private void playSuccessSound() {
        String path = getClass().getResource("/sounds/voter1.mp3").toExternalForm();
        if (path == null) {
            System.out.println("Sound file not found: /sounds/voter1.mp3");
            return;
        }

        Media sound = new Media(path);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void applyScaleEffect(Button button) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        button.setOnMouseEntered(e -> scaleIn.playFromStart());
        button.setOnMouseExited(e -> scaleOut.playFromStart());
    }

    private void applyScaleEffectForImageView(ImageView imageView) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), imageView);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), imageView);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        imageView.setOnMouseEntered(e -> scaleIn.playFromStart());
        imageView.setOnMouseExited(e -> scaleOut.playFromStart());
    }

    private void applyScaleEffectForTextField(TextField textField) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), textField);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), textField);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        textField.setOnMouseEntered(e -> scaleIn.playFromStart());
        textField.setOnMouseExited(e -> scaleOut.playFromStart());
    }

    @FXML
    public void initialize() {
        applyScaleEffect(backButton);
        applyScaleEffect(loadVotersButton);
        applyScaleEffect(addVoterButton);
        applyScaleEffect(uploadButton);
        applyScaleEffectForImageView(voterImageView);
        applyScaleEffectForTextField(voterNameField);
        applyScaleEffectForTextField(voterCnicField);
        applyScaleEffectForTextField(voterFamilyNumberField);
    }
}
