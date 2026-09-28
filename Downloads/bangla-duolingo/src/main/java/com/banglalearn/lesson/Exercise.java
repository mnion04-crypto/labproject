package com.banglalearn.lesson;

import com.banglalearn.model.LessonItem;
import java.util.List;

/**
 * Base class for every quiz style. Holds the shared state and logic;
 * subclasses decide WHAT is asked and WHAT counts as the answer.
 */
public abstract class Exercise {

    private final LessonItem sourceItem;
    private List<String> options;
    private int correctOptionIndex;

    protected Exercise(LessonItem sourceItem) {
        this.sourceItem = sourceItem;
    }

    // ---- Subclasses MUST implement these ----
    public abstract ExerciseType type();
    public abstract String prompt();
    public abstract String answerFor(LessonItem item);

    // ---- Shared behaviour ----
    void setOptions(List<String> options, int correctOptionIndex) {
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public LessonItem sourceItem() { return sourceItem; }
    public List<String> options() { return options; }

    public boolean isCorrect(int chosenIndex) {
        return chosenIndex == correctOptionIndex;
    }

    public String correctAnswerText() {
        return options.get(correctOptionIndex);
    }
}