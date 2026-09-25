package com.banglalearn.ui;

import com.banglalearn.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label currentUserLabel;

    @FXML
    public void initialize() {
        var profile = AppSession.currentProfile();
        currentUserLabel.setText(profile != null ? profile.name() : "");
        showLessons();
    }

    @FXML
    private void showLessons() {
        swapContent("/fxml/lesson-view.fxml");
    }

    @FXML
    private void showVocabulary() {
        swapContent("/fxml/vocabulary-view.fxml");
    }

    @FXML
    private void showProfile() {
        swapContent("/fxml/profile-view.fxml");
    }

    @FXML
    private void handleSwitchProfile() {
        try {
            AppSession.setCurrentProfile(null);
            Main.showProfilePicker();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void swapContent(String fxmlPath) {
        try {
            Parent view = new FXMLLoader(getClass().getResource(fxmlPath)).load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}
