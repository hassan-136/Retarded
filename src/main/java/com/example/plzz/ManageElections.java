package com.example.plzz;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class ManageElections {

    @FXML private TextField electionNameField;
    @FXML private TextField electionTypeField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private DatePicker openVoteDatePicker;
    @FXML private DatePicker closeVoteDatePicker;
    @FXML private TextArea electionRulesField;

    @FXML private Button createElectionButton;
    @FXML private Button loadCandidatesButton;
    @FXML private Button linkCandidatesButton;
    @FXML private Button generateReportButton;
    @FXML private Button backButton;


    @FXML private ListView<HBox> candidatesListView;
    @FXML private ListView<HBox> pastElectionsListView;  // Updated to display HBox with delete button

    private ObservableList<HBox> candidates = FXCollections.observableArrayList();
    private ObservableList<HBox> pastElections = FXCollections.observableArrayList();  // List of HBox for past elections

    private List<String> linkedCandidates = List.of();

    private final String symbolSaveDir = "resources/images/candidates/";
    private final String dataSaveDir = "resources/data/";
    private final String candidateDataFile = dataSaveDir + "candidates.txt";
    private final String electionDataFile = dataSaveDir + "elections.txt";



    @FXML
    private void handleBack() {
        backButton.getScene().getWindow().hide();
    }


    @FXML
    public void initialize() {
        candidatesListView.setItems(candidates);
        pastElectionsListView.setItems(pastElections);
        loadPastElections();
        applyScaleOnHover(electionNameField);
        applyScaleOnHover(electionTypeField);
        applyScaleOnHover(startDatePicker);
        applyScaleOnHover(endDatePicker);
        applyScaleOnHover(openVoteDatePicker);
        applyScaleOnHover(closeVoteDatePicker);
        applyScaleOnHover(electionRulesField);
        applyScaleOnHover(createElectionButton);
        applyScaleOnHover(loadCandidatesButton);
        applyScaleOnHover(linkCandidatesButton);
        applyScaleOnHover(generateReportButton);
        applyScaleOnHover(backButton);
    }


    @FXML
    private void handleLoadCandidates() {
        File file = new File(candidateDataFile);
        if (!file.exists()) {
            showAlert("Error", "Candidate file not found.", Alert.AlertType.ERROR);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            candidates.clear();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",", 6);
                    if (parts.length >= 6) {
                        String name = parts[0].trim();
                        String cnic = parts[1].trim();
                        String symbolPath = parts[4].trim();
                        String imagePath = parts[5].trim();

                        ImageView profileImageView = new ImageView(new Image(new File(imagePath).toURI().toString()));
                        profileImageView.setFitWidth(40);
                        profileImageView.setFitHeight(40);

                        ImageView symbolImageView = new ImageView(new Image(new File(symbolPath).toURI().toString()));
                        symbolImageView.setFitWidth(40);
                        symbolImageView.setFitHeight(40);

                        Label nameLabel = new Label(name);
                        nameLabel.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 0 10px;");

                        Button deleteBtn = new Button("Delete");
                        deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        deleteBtn.setOnAction(e -> handleDeleteCandidate(cnic, symbolPath, imagePath));

                        HBox hbox = new HBox(profileImageView, symbolImageView, nameLabel, deleteBtn);
                        hbox.setSpacing(20);

                        candidates.add(hbox);
                    }
                }
            }
            candidatesListView.setItems(candidates);
            showAlert("Success", "Candidates loaded successfully.", Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("Error", "Failed to load candidates: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void handleDeleteCandidate(String cnic, String symbolPath, String imagePath) {
        File file = new File(candidateDataFile);
        StringBuilder updatedContent = new StringBuilder();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length >= 2 && parts[1].equals(cnic)) {
                    deleted = true;
                    continue;
                }
                updatedContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (deleted) {
            deleteFile(symbolPath);
            deleteFile(imagePath);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(updatedContent.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            handleLoadCandidates(); // Refresh list
            showAlert("Deleted", "Candidate deleted successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Not Found", "Candidate with CNIC not found!", Alert.AlertType.WARNING);
        }
    }

    private void deleteFile(String path) {
        if (path != null) {
            File f = new File(path);
            if (f.exists()) f.delete();
        }
    }

    @FXML
    private void handleLinkCandidates() {
        if (candidates.isEmpty()) {
            showAlert("No Candidates", "Please load candidates first.", Alert.AlertType.WARNING);
            return;
        }
        linkedCandidates = candidates.stream()
                .map(hbox -> {
                    for (var node : hbox.getChildren()) {
                        if (node instanceof Label label) return label.getText();
                    }
                    return "";
                })
                .toList();

        showAlert("Success", "Candidates linked to election successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleGenerateReport() {
        StringBuilder report = buildElectionDetails();
        showAlert("Election Report", report.toString(), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCreateElection() {
        String name = electionNameField.getText();
        String type = electionTypeField.getText();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        LocalDate open = openVoteDatePicker.getValue();
        LocalDate close = closeVoteDatePicker.getValue();
        String rules = electionRulesField.getText();

        if (name.isEmpty() || type.isEmpty() || start == null || end == null || open == null || close == null || rules.isEmpty() || linkedCandidates.isEmpty()) {
            showAlert("Missing Data", "Please complete all fields and link candidates before creating an election.", Alert.AlertType.WARNING);
            return;
        }

        LocalDate today = LocalDate.now();
        if (start.isBefore(today) || end.isBefore(today) || open.isBefore(today) || close.isBefore(today)) {
            showAlert("Invalid Dates", "All dates must be today or in the future.", Alert.AlertType.ERROR);
            return;
        }

        if (end.isBefore(start)) {
            showAlert("Invalid Dates", "End date must be after start date.", Alert.AlertType.ERROR);
            return;
        }

        if (close.isBefore(open)) {
            showAlert("Invalid Dates", "Close vote date must be after open vote date.", Alert.AlertType.ERROR);
            return;
        }
        if (start.isAfter(open) || end.isBefore(close) || open.isBefore(start) || close.isAfter(end)) {
            showAlert("Invalid Dates", "Election start date must be on or before the open voting date,\n" +
                    "election end date must be on or after the close voting date,\n" +
                    "and open/close voting dates must lie within the election period.", Alert.AlertType.ERROR);
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(electionDataFile, true))) {
            writer.write("Election Name: " + name + "\n");
            writer.write("Election Type: " + type + "\n");
            writer.write("Start Date: " + start + "\n");
            writer.write("End Date: " + end + "\n");
            writer.write("Open Voting: " + open + "\n");
            writer.write("Close Voting: " + close + "\n");
            writer.write("Rules: " + rules + "\n");
            writer.write("Candidates:\n");
            for (String candidate : linkedCandidates) {
                writer.write("- " + candidate + "\n");
            }
            writer.write("------\n");
        } catch (IOException e) {
            showAlert("Error", "Failed to save election: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        pastElections.add(new HBox(new Label(name + " (" + start + " to " + end + ")"), createDeleteElectionButton(name)));
        playSuccessSound();
        showAlert("Success", "Election '" + name + "' created and saved!", Alert.AlertType.INFORMATION);

        clearForm();
    }


    private Button createDeleteElectionButton(String electionName) {
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> handleDeletePastElection(electionName));
        return deleteBtn;
    }

    private void loadPastElections() {
        File file = new File(electionDataFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String name = null;
            String start = null;
            String end = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Election Name: ")) {
                    name = line.substring(15);
                } else if (line.startsWith("Start Date: ")) {
                    start = line.substring(12);
                } else if (line.startsWith("End Date: ")) {
                    end = line.substring(10);
                } else if (line.equals("------") && name != null && start != null && end != null) {
                    String electionInfo = name + " (" + start + " to " + end + ")";
                    pastElections.add(new HBox(new Label(electionInfo), createDeleteElectionButton(name)));
                    name = start = end = null;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading past elections: " + e.getMessage());
        }
    }

    private void handleDeletePastElection(String electionName) {
        File file = new File(electionDataFile);
        StringBuilder updatedContent = new StringBuilder();
        boolean skipBlock = false;
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Election Name: ")) {
                    String name = line.substring(15);
                    skipBlock = electionName.contains(name);
                    if (skipBlock) found = true;
                }

                if (!skipBlock) {
                    updatedContent.append(line).append("\n");
                }

                if (line.equals("------")) {
                    skipBlock = false;
                }
            }

            if (found) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(updatedContent.toString());
                }
                pastElections.clear();
                loadPastElections();

                showAlert("Success", "Past election deleted successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Not Found", "Election not found in records.", Alert.AlertType.WARNING);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder buildElectionDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Election Report\n====================\n");
        sb.append("Election Name: ").append(electionNameField.getText()).append("\n");
        sb.append("Election Type: ").append(electionTypeField.getText()).append("\n");
        sb.append("Start Date: ").append(startDatePicker.getValue()).append("\n");
        sb.append("End Date: ").append(endDatePicker.getValue()).append("\n");
        sb.append("Open Vote Date: ").append(openVoteDatePicker.getValue()).append("\n");
        sb.append("Close Vote Date: ").append(closeVoteDatePicker.getValue()).append("\n");
        sb.append("Election Rules:\n").append(electionRulesField.getText()).append("\n\n");
        sb.append("Linked Candidates:\n");
        for (String candidate : linkedCandidates) {
            sb.append("- ").append(candidate).append("\n");
        }
        return sb;
    }

    private void clearForm() {
        electionNameField.clear();
        electionTypeField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        openVoteDatePicker.setValue(null);
        closeVoteDatePicker.setValue(null);
        electionRulesField.clear();
        linkedCandidates = List.of();
    }

    private void playSuccessSound() {
        String path = getClass().getResource("/sounds/elec.mp3").toExternalForm();
        Media sound = new Media(path);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
    private void applyScaleOnHover(javafx.scene.Node node) {
        node.setOnMouseEntered(e -> {
            node.setScaleX(1.05);
            node.setScaleY(1.05);
        });

        node.setOnMouseExited(e -> {
            node.setScaleX(1.0);
            node.setScaleY(1.0);
        });
    }
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
