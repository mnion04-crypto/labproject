package com.banglalearn.ui;

import com.banglalearn.model.BanglaWord;
import com.banglalearn.model.PartOfSpeech;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

/** Pop-up form for adding or editing a word. */
public final class WordDialog {

    public record WordFormData(String bangla, String romanization, String english,
                               PartOfSpeech partOfSpeech, String group) {
    }

    private WordDialog() {
    }

    /** existing == null means "add a new word". Returns empty if the user cancels. */
    public static Optional<WordFormData> show(BanglaWord existing, List<String> groups) {
        Dialog<WordFormData> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add word" : "Edit word");
        dialog.setHeaderText(existing == null ? "Add a new word" : "Edit this word");
        dialog.getDialogPane().getStylesheets()
                .add(WordDialog.class.getResource("/css/style.css").toExternalForm());

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        TextField banglaField = new TextField();
        TextField romanField = new TextField();
        TextField englishField = new TextField();
        ComboBox<PartOfSpeech> posBox =
                new ComboBox<>(FXCollections.observableArrayList(PartOfSpeech.values()));
        ComboBox<String> groupBox = new ComboBox<>(FXCollections.observableArrayList(groups));
        groupBox.setEditable(true);
        groupBox.setPromptText("pick one or type a new group");

        if (existing != null) {
            banglaField.setText(existing.bangla());
            romanField.setText(existing.romanization());
            englishField.setText(existing.englishMeaning());
            posBox.setValue(existing.partOfSpeech());
            groupBox.setValue(existing.lessonGroup());
        } else {
            posBox.setValue(PartOfSpeech.NOUN);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.addRow(0, new Label("Bangla:"), banglaField);
        grid.addRow(1, new Label("Romanization:"), romanField);
        grid.addRow(2, new Label("English meaning:"), englishField);
        grid.addRow(3, new Label("Part of speech:"), posBox);
        grid.addRow(4, new Label("Lesson group:"), groupBox);
        dialog.getDialogPane().setContent(grid);

        // Keep "Save" disabled until every field is filled in.
        Node saveButton = dialog.getDialogPane().lookupButton(saveType);
        Runnable validate = () -> saveButton.setDisable(
                banglaField.getText().isBlank()
                        || romanField.getText().isBlank()
                        || englishField.getText().isBlank()
                        || posBox.getValue() == null
                        || groupBox.getEditor().getText().isBlank());
        banglaField.textProperty().addListener((o, a, b) -> validate.run());
        romanField.textProperty().addListener((o, a, b) -> validate.run());
        englishField.textProperty().addListener((o, a, b) -> validate.run());
        posBox.valueProperty().addListener((o, a, b) -> validate.run());
        groupBox.getEditor().textProperty().addListener((o, a, b) -> validate.run());
        validate.run();

        dialog.setResultConverter(button -> {
            if (button != saveType) return null;
            return new WordFormData(
                    banglaField.getText().trim(),
                    romanField.getText().trim(),
                    englishField.getText().trim(),
                    posBox.getValue(),
                    groupBox.getEditor().getText().trim().toLowerCase());
        });

        return dialog.showAndWait();
    }
}