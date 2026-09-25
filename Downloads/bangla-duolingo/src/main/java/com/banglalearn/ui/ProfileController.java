package com.banglalearn.ui;

import com.banglalearn.db.LessonProgress;
import com.banglalearn.db.ProgressDAO;
import com.banglalearn.db.UserProfile;
import com.banglalearn.util.BackgroundTasks;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfileController {

    @FXML private Label headingLabel;
    @FXML private Label summaryLabel;
    @FXML private TableView<LessonProgress> progressTable;
    @FXML private TableColumn<LessonProgress, String> groupColumn;
    @FXML private TableColumn<LessonProgress, String> scoreColumn;
    @FXML private TableColumn<LessonProgress, String> perfectColumn;
    @FXML private TableColumn<LessonProgress, String> dateColumn;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    @FXML
    public void initialize() {
        UserProfile profile = AppSession.currentProfile();
        headingLabel.setText(profile != null ? profile.name() + "'s progress" : "Progress");

        groupColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().lessonGroup()));
        scoreColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().scorePercent() + "%"));
        perfectColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().perfectScore() ? "★" : ""));
        dateColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().completedAt().format(DATE_FORMAT)));

        if (profile != null) {
            loadProgress(profile);
        }
    }

    private void loadProgress(UserProfile profile) {
        BackgroundTasks.run(
                () -> new ProgressDAO().getProgressForProfile(profile.id()),
                (List<LessonProgress> rows) -> {
                    progressTable.setItems(FXCollections.observableArrayList(rows));
                    long lessonsCompleted = rows.size();
                    long perfects = rows.stream().filter(LessonProgress::perfectScore).count();
                    summaryLabel.setText(lessonsCompleted + " lessons completed, " + perfects + " with a perfect score");
                },
                error -> summaryLabel.setText("Could not load progress: " + error.getMessage())
        );
    }
}
