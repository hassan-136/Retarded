package com.example.plzz;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ManageCandidates {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField cnicField;

    @FXML
    private TextField partyField;

    @FXML
    private TextField symbolField;

    @FXML
    private ImageView symbolImageView;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button uploadSymbolButton;

    @FXML
    private Button uploadProfileButton;

    @FXML
    private Button addButton;

    @FXML
    private Button loadCandidatesButton;

    @FXML
    private ListView<HBox> candidateListView;

    @FXML
    private Button backButton;

    private File selectedSymbolFile;
    private File selectedProfileFile;

    private final String symbolSaveDir = "resources/images/candidates/";
    private final String dataSaveDir = "resources/data/";
    private final String candidateDataFile = dataSaveDir + "candidates.txt";

    @FXML
    private void initialize() {
        uploadSymbolButton.setOnAction(e -> chooseImage(true));
        uploadProfileButton.setOnAction(e -> chooseImage(false));
        addButton.setOnAction(e -> addCandidate());
        loadCandidatesButton.setOnAction(e -> toggleCandidatesListView());

        uploadSymbolButton.toFront();
        uploadProfileButton.toFront();
        symbolImageView.toFront();
        profileImageView.toFront();

        // Hover Effects
        addHoverEffect(fullNameField);
        addHoverEffect(cnicField);
        addHoverEffect(partyField);
        addHoverEffect(symbolField);
        addHoverEffect(uploadSymbolButton);
        addHoverEffect(uploadProfileButton);
        addHoverEffect(addButton);
        addHoverEffect(loadCandidatesButton);
        addHoverEffect(backButton);
        addHoverEffect(symbolImageView);
        addHoverEffect(profileImageView);

        loadCandidatesToListView();
    }

    private void addHoverEffect(javafx.scene.Node node) {
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
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void chooseImage(boolean isSymbol) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) uploadSymbolButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Image img = new Image(file.toURI().toString());
                if (isSymbol) {
                    symbolImageView.setImage(img);
                    selectedSymbolFile = file;
                } else {
                    profileImageView.setImage(img);
                    selectedProfileFile = file;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Unable to load image.");
            }
        }
    }

    private void addCandidate() {
        String fullName = fullNameField.getText().trim();
        String cnic = cnicField.getText().trim();
        String party = partyField.getText().trim();
        String symbol = symbolField.getText().trim();

        if (fullName.isEmpty() || cnic.isEmpty() || party.isEmpty() || symbol.isEmpty()
                || selectedSymbolFile == null || selectedProfileFile == null) {
            showAlert("Validation Error", "Please fill all fields and upload both images.");
            return;
        }

        if (!cnic.matches("\\d{13}")) {
            showAlert("Validation Error", "CNIC must be 13 digits long and only contain numbers.");
            return;
        }

        try {
            Files.createDirectories(Paths.get(symbolSaveDir));
            Files.createDirectories(Paths.get(dataSaveDir));

            String symbolImageName = "symbol_" + cnic + getExtension(selectedSymbolFile.getName());
            String profileImageName = "profile_" + cnic + getExtension(selectedProfileFile.getName());

            String symbolImagePath = symbolSaveDir + symbolImageName;
            String profileImagePath = symbolSaveDir + profileImageName;

            Files.copy(selectedSymbolFile.toPath(), Paths.get(symbolImagePath), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(selectedProfileFile.toPath(), Paths.get(profileImagePath), StandardCopyOption.REPLACE_EXISTING);

            String line = String.join(",", fullName, cnic, party, symbol, symbolImagePath, profileImagePath);
            Path filePath = Paths.get(candidateDataFile);
            Files.write(filePath, (line + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            playSuccessSoundAndWait("Success", "Candidate added successfully.");

            clearForm();
            loadCandidatesToListView();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "An error occurred while saving candidate.");
        }
    }

    private void loadCandidatesToListView() {
        candidateListView.getItems().clear();
        candidateListView.setStyle("-fx-control-inner-background: #FFFFFF40;");

        try {
            Path filePath = Paths.get(candidateDataFile);
            if (!Files.exists(filePath)) return;

            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String fullName = parts[0];
                    String cnic = parts[1];
                    String party = parts[2];
                    String symbol = parts[3];
                    String symbolPath = parts[4];
                    String profilePath = parts[5];

                    ImageView profileView = new ImageView(new Image(new File(profilePath).toURI().toString()));
                    profileView.setFitHeight(60);
                    profileView.setFitWidth(60);

                    ImageView symbolView = new ImageView(new Image(new File(symbolPath).toURI().toString()));
                    symbolView.setFitHeight(60);
                    symbolView.setFitWidth(60);

                    String textInfo = "Name: " + fullName + "\nCNIC: " + cnic + "\nParty: " + party + "\nSymbol: " + symbol;
                    TextArea textArea = new TextArea(textInfo);
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    textArea.setPrefHeight(60);
                    textArea.setPrefWidth(300);

                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(e -> handleDeleteCandidate(cnic, symbolPath, profilePath));
                    addHoverEffect(deleteButton);

                    HBox hBox = new HBox(10, profileView, symbolView, textArea, deleteButton);
                    hBox.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");
                    candidateListView.getItems().add(hBox);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteCandidate(String cnic, String symbolPath, String profilePath) {
        File file = new File(candidateDataFile);
        StringBuilder updatedContent = new StringBuilder();
        boolean candidateDeleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[1].equals(cnic)) {
                    candidateDeleted = true;
                    deleteImage(symbolPath);
                    deleteImage(profilePath);
                    continue;
                }
                updatedContent.append(line).append("\n");
            }

            if (candidateDeleted) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(updatedContent.toString());
                }

                loadCandidatesToListView();
                showAlert("Success", "Candidate deleted successfully!");
            } else {
                showAlert("Error", "Candidate not found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteImage(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex != -1) ? filename.substring(dotIndex) : "";
    }

    private void playSuccessSoundAndWait(String title, String message) {
        MediaPlayer mediaPlayer = null;
        try {
            String path = getClass().getResource("/sounds/candidate.mp3").toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Failed to play sound: " + e.getMessage());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void clearForm() {
        fullNameField.clear();
        cnicField.clear();
        partyField.clear();
        symbolField.clear();

        String defaultSymbolImagePath = "file:D:/plzz/resources/images/symbol.png";
        String defaultProfileImagePath = "file:D:/plzz/resources/images/pf.png";

        symbolImageView.setImage(new Image(defaultSymbolImagePath));
        profileImageView.setImage(new Image(defaultProfileImagePath));

        selectedSymbolFile = null;
        selectedProfileFile = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void toggleCandidatesListView() {
        boolean isCurrentlyVisible = candidateListView.isVisible();
        candidateListView.setVisible(!isCurrentlyVisible);
        candidateListView.setManaged(!isCurrentlyVisible);
    }
}
