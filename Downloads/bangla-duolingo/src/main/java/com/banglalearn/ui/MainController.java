package com.banglalearn.ui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
    @FXML private BorderPane root;
    @FXML private VBox sidebar;
    @FXML
    public void initialize() {
        // Sidebar = 22% of window width (clamped by minWidth/maxWidth in the FXML)
        sidebar.prefWidthProperty().bind(root.widthProperty().multiply(0.22));

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
