package com.banglalearn.ui;

import javafx.scene.control.Alert;
import com.banglalearn.data.ContentLoader;
import com.banglalearn.db.ProgressDAO;
import com.banglalearn.db.UserProfile;
import com.banglalearn.lesson.Exercise;
import com.banglalearn.lesson.ExerciseType;
import com.banglalearn.lesson.LessonFactory;
import com.banglalearn.lesson.QuizSession;
import com.banglalearn.model.LessonItem;
import com.banglalearn.util.BackgroundTasks;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LessonController {

    @FXML private VBox groupSelectionPane;
    @FXML private VBox quizPane;
    @FXML private VBox resultsPane;

    @FXML private Label loadingLabel;
    @FXML private FlowPane groupButtonsPane;

    @FXML private Label quizGroupLabel;
    @FXML private ProgressBar quizProgressBar;
    @FXML private Label promptLabel;
    @FXML private FlowPane optionsPane;
    @FXML private Label feedbackLabel;
    @FXML private Button nextButton;

    @FXML private Label resultsHeading;
    @FXML private Label resultsScoreLabel;

    private final LessonFactory lessonFactory = new LessonFactory();
    private final Map<String, List<LessonItem>> lessonGroups = new LinkedHashMap<>();

    private QuizSession activeSession;
    private boolean answeredCurrent = false;

    @FXML
    public void initialize() {
        loadContent();
    }

    private void loadContent() {
        BackgroundTasks.run(
                () -> {
                    ContentLoader loader = new ContentLoader();
                    Map<String, List<LessonItem>> groups = new LinkedHashMap<>();
                    groupItemsByLessonGroup(groups, loader.loadCharacters().stream().map(c -> (LessonItem) c).toList());
                    groupItemsByLessonGroup(groups, loader.loadWords().stream().map(w -> (LessonItem) w).toList());
                    return groups;
                },
                (Map<String, List<LessonItem>> groups) -> {
                    lessonGroups.clear();
                    lessonGroups.putAll(groups);
                    loadingLabel.setText("");
                    renderGroupButtons();
                },
                error -> loadingLabel.setText("Failed to load lessons: " + error.getMessage())
        );
    }

    private void groupItemsByLessonGroup(Map<String, List<LessonItem>> target, List<LessonItem> items) {
        for (LessonItem item : items) {
            target.computeIfAbsent(item.lessonGroup(), g -> new java.util.ArrayList<>()).add(item);
        }
    }

    private void renderGroupButtons() {
        groupButtonsPane.getChildren().clear();
        UserProfile profile = AppSession.currentProfile();

        for (String group : lessonGroups.keySet()) {
            Button btn = new Button(displayName(group));
            btn.getStyleClass().add("lesson-group-btn");
            btn.setOnAction(e -> startQuiz(group));
            groupButtonsPane.getChildren().add(btn);

            if (profile != null) {
                BackgroundTasks.run(
                        () -> new ProgressDAO().hasPerfectScore(profile.id(), group),
                        (Boolean perfect) -> {
                            if (Boolean.TRUE.equals(perfect)) {
                                btn.getStyleClass().add("perfect");
                                btn.setText("★ " + displayName(group));
                            }
                        },
                        error -> { /* non-critical: just skip the badge */ }
                );
            }
        }
    }

    private String displayName(String group) {
        return group.substring(0, 1).toUpperCase() + group.substring(1);
    }

    private void startQuiz(String group) {
        List<LessonItem> items = lessonGroups.get(group);
        List<Exercise> exercises = lessonFactory.buildLesson(items, ExerciseType.ENGLISH_TO_BANGLA);
        activeSession = new QuizSession(group, exercises);

        quizGroupLabel.setText(displayName(group) + " lesson");
        showPane(quizPane);
        renderCurrentExercise();
    }

    private void renderCurrentExercise() {
        answeredCurrent = false;
        nextButton.setVisible(false);
        feedbackLabel.setText("");
        optionsPane.getChildren().clear();

        Exercise exercise = activeSession.currentExercise();
        promptLabel.setText(exercise.prompt());
        quizProgressBar.setProgress((double) (activeSession.currentIndexOneBased() - 1) / activeSession.totalExercises());

        for (int i = 0; i < exercise.options().size(); i++) {
            String optionText = exercise.options().get(i);
            Button optionButton = new Button(optionText);
            optionButton.getStyleClass().add("option-button");
            int optionIndex = i;
            optionButton.setOnAction(e -> handleAnswer(optionIndex, optionButton));
            optionsPane.getChildren().add(optionButton);
        }
    }

    private void handleAnswer(int chosenIndex, Button clicked) {
        if (answeredCurrent) return;
        answeredCurrent = true;

        Exercise exercise = activeSession.currentExercise();
        boolean correct = exercise.isCorrect(chosenIndex);

        for (var node : optionsPane.getChildren()) {
            node.setDisable(true);
        }
        clicked.getStyleClass().add(correct ? "correct" : "incorrect");

        if (correct) {
            feedbackLabel.setText("Correct!");
            feedbackLabel.getStyleClass().setAll("feedback-label", "feedback-correct");
        } else {
            feedbackLabel.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Incorrect");
            alert.setHeaderText("Not quite!");
            alert.setContentText("Correct answer: " + exercise.correctAnswerText());
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            alert.showAndWait();
        }

        activeSession.submitAnswer(chosenIndex);
        nextButton.setVisible(true);
    }

    @FXML
    private void handleNext() {
        if (activeSession.isFinished()) {
            finishQuiz();
        } else {
            renderCurrentExercise();
        }
    }

    private void finishQuiz() {
        int score = activeSession.scorePercent();
        boolean perfect = activeSession.isPerfectScore();
        String group = activeSession.lessonGroup();

        resultsHeading.setText(perfect ? "Perfect score!" : "Lesson complete");
        resultsScoreLabel.setText(activeSession.correctCount() + " / " + activeSession.totalExercises()
                + " correct (" + score + "%)");
        showPane(resultsPane);

        UserProfile profile = AppSession.currentProfile();
        if (profile != null) {
            BackgroundTasks.run(
                    () -> {
                        new ProgressDAO().recordLessonCompletion(profile.id(), group, score, perfect);
                        return true;
                    },
                    ok -> renderGroupButtons(),
                    error -> System.err.println("Failed to save progress: " + error.getMessage())
            );
        }
    }

    @FXML
    private void handleExitQuiz() {
        showPane(groupSelectionPane);
    }

    private void showPane(VBox paneToShow) {
        for (VBox pane : List.of(groupSelectionPane, quizPane, resultsPane)) {
            boolean show = pane == paneToShow;
            pane.setVisible(show);
            pane.setManaged(show);
        }
    }
}
