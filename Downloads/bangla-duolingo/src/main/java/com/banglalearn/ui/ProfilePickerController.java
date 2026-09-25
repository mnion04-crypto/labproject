package com.banglalearn.ui;

import com.banglalearn.Main;
import com.banglalearn.db.ProgressDAO;
import com.banglalearn.db.UserProfile;
import com.banglalearn.util.BackgroundTasks;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class ProfilePickerController {

    @FXML private ListView<UserProfile> profileListView;
    @FXML private Button continueButton;
    @FXML private TextField newProfileField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        loadProfiles();
        profileListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> continueButton.setDisable(newVal == null));
    }

    private void loadProfiles() {
        BackgroundTasks.run(
                () -> new ProgressDAO().getAllProfiles(),
                (List<UserProfile> profiles) -> profileListView.getItems().setAll(profiles),
                error -> showError("Could not load profiles: " + error.getMessage())
        );
    }

    @FXML
    private void handleContinue() {
        UserProfile selected = profileListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        enterAppAs(selected);
    }

    @FXML
    private void handleCreateProfile() {
        String name = newProfileField.getText() == null ? "" : newProfileField.getText().trim();
        if (name.isEmpty()) {
            showError("Enter a name for the new profile.");
            return;
        }
        BackgroundTasks.run(
                () -> new ProgressDAO().createProfile(name),
                (UserProfile created) -> enterAppAs(created),
                error -> showError("Could not create profile: " + error.getMessage())
        );
    }

    private void enterAppAs(UserProfile profile) {
        AppSession.setCurrentProfile(profile);
        try {
            Main.showMainShell();
        } catch (IOException e) {
            showError("Could not open the app: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }
}
