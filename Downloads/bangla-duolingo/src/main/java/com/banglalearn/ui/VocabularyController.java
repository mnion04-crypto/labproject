package com.banglalearn.ui;

import com.banglalearn.data.ContentLoader;
import com.banglalearn.db.WordDAO;
import com.banglalearn.model.BanglaWord;
import com.banglalearn.model.LessonItem;
import com.banglalearn.util.BackgroundTasks;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VocabularyController {

    @FXML private ComboBox<String> groupComboBox;
    @FXML private TableView<LessonItem> itemsTable;
    @FXML private TableColumn<LessonItem, String> banglaColumn;
    @FXML private TableColumn<LessonItem, String> romanizationColumn;
    @FXML private TableColumn<LessonItem, String> meaningColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private final List<LessonItem> allItems = new ArrayList<>();

    @FXML
    public void initialize() {
        banglaColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().bangla()));
        romanizationColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().romanization()));
        meaningColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().englishMeaning()));

        groupComboBox.setOnAction(e -> filterByGroup(groupComboBox.getValue()));

        // Edit/Delete are only enabled for words the user added themselves.
        itemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, selected) -> {
            boolean editable = selected instanceof BanglaWord w && WordDAO.isCustom(w);
            editButton.setDisable(!editable);
            deleteButton.setDisable(!editable);
        });

        loadContent();
    }

    private void loadContent() {
        BackgroundTasks.run(
                () -> {
                    ContentLoader loader = new ContentLoader();
                    List<LessonItem> combined = new ArrayList<>();
                    combined.addAll(loader.loadCharacters());
                    combined.addAll(loader.loadWords());
                    combined.addAll(new WordDAO().getAll());   // NEW: user-added words
                    return combined;
                },
                (List<LessonItem> items) -> {
                    String keep = groupComboBox.getValue();     // NEW: remember current filter
                    allItems.clear();
                    allItems.addAll(items);
                    populateGroupChoices(items, keep);
                },
                error -> statusLabel.setText("Failed to load vocabulary: " + error.getMessage())
        );
    }

    private void populateGroupChoices(List<LessonItem> items, String keepSelection) {
        Set<String> groups = new LinkedHashSet<>();
        groups.add("All");
        for (LessonItem item : items) {
            groups.add(item.lessonGroup());
        }
        groupComboBox.setItems(FXCollections.observableArrayList(groups));
        String toSelect = (keepSelection != null && groups.contains(keepSelection)) ? keepSelection : "All";
        groupComboBox.getSelectionModel().select(toSelect);
        filterByGroup(toSelect);
    }

    private void filterByGroup(String group) {
        ObservableList<LessonItem> filtered = FXCollections.observableArrayList();
        for (LessonItem item : allItems) {
            if ("All".equals(group) || item.lessonGroup().equals(group)) {
                filtered.add(item);
            }
        }
        itemsTable.setItems(filtered);
    }

    private List<String> groupNames() {
        return groupComboBox.getItems().stream().filter(g -> !"All".equals(g)).toList();
    }

    // ---------- NEW: add / edit / delete ----------

    @FXML
    private void handleAddWord() {
        WordDialog.show(null, groupNames()).ifPresent(data -> BackgroundTasks.run(
                () -> new WordDAO().add(data.bangla(), data.romanization(), data.english(),
                        data.partOfSpeech(), data.group()),
                (BanglaWord added) -> {
                    statusLabel.setText("Added: " + added.bangla() + " (" + added.englishMeaning() + ")");
                    loadContent();
                },
                error -> statusLabel.setText("Could not add word: " + error.getMessage())
        ));
    }

    @FXML
    private void handleEditWord() {
        LessonItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (!(selected instanceof BanglaWord)) return;
        BanglaWord word = (BanglaWord) selected;
        if (!WordDAO.isCustom(word)) return;

        WordDialog.show(word, groupNames()).ifPresent(data -> {
            BanglaWord updated = new BanglaWord(word.id(), data.bangla(), data.romanization(),
                    data.english(), data.partOfSpeech(), data.group(), word.difficulty());
            BackgroundTasks.run(
                    () -> {
                        new WordDAO().update(updated);
                        return true;
                    },
                    (Boolean ok) -> {
                        statusLabel.setText("Updated: " + updated.bangla());
                        loadContent();
                    },
                    error -> statusLabel.setText("Could not update word: " + error.getMessage())
            );
        });
    }

    @FXML
    private void handleDeleteWord() {
        LessonItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (!(selected instanceof BanglaWord)) return;
        BanglaWord word = (BanglaWord) selected;
        if (!WordDAO.isCustom(word)) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + word.bangla() + "\" (" + word.englishMeaning() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Delete word");
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> BackgroundTasks.run(
                () -> {
                    new WordDAO().delete(word.id());
                    return true;
                },
                (Boolean ok) -> {
                    statusLabel.setText("Deleted: " + word.bangla());
                    loadContent();
                },
                error -> statusLabel.setText("Could not delete word: " + error.getMessage())
        ));
    }
}