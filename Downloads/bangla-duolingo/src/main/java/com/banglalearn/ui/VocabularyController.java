package com.banglalearn.ui;

import com.banglalearn.data.ContentLoader;
import com.banglalearn.model.LessonItem;
import com.banglalearn.util.BackgroundTasks;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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

    private final List<LessonItem> allItems = new ArrayList<>();

    @FXML
    public void initialize() {
        banglaColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().bangla()));
        romanizationColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().romanization()));
        meaningColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().englishMeaning()));

        groupComboBox.setOnAction(e -> filterByGroup(groupComboBox.getValue()));

        loadContent();
    }

    private void loadContent() {
        BackgroundTasks.run(
                () -> {
                    ContentLoader loader = new ContentLoader();
                    List<LessonItem> combined = new ArrayList<>();
                    combined.addAll(loader.loadCharacters());
                    combined.addAll(loader.loadWords());
                    return combined;
                },
                (List<LessonItem> items) -> {
                    allItems.clear();
                    allItems.addAll(items);
                    populateGroupChoices(items);
                },
                error -> System.err.println("Failed to load vocabulary: " + error.getMessage())
        );
    }

    private void populateGroupChoices(List<LessonItem> items) {
        Set<String> groups = new LinkedHashSet<>();
        groups.add("All");
        for (LessonItem item : items) {
            groups.add(item.lessonGroup());
        }
        groupComboBox.setItems(FXCollections.observableArrayList(groups));
        groupComboBox.getSelectionModel().select("All");
        filterByGroup("All");
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
}
