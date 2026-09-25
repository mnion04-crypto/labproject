package com.banglalearn.lesson;

import com.banglalearn.model.LessonItem;

import java.util.List;

/**
 * One multiple-choice question: a prompt string, a shuffled list of
 * option strings, and which option index is correct.
 */
public class Exercise {

    private final LessonItem sourceItem;
    private final ExerciseType type;
    private final String prompt;
    private final List<String> options;
    private final int correctOptionIndex;

    public Exercise(LessonItem sourceItem, ExerciseType type, String prompt,
                     List<String> options, int correctOptionIndex) {
        this.sourceItem = sourceItem;
        this.type = type;
        this.prompt = prompt;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public LessonItem sourceItem() {
        return sourceItem;
    }

    public ExerciseType type() {
        return type;
    }

    public String prompt() {
        return prompt;
    }

    public List<String> options() {
        return options;
    }

    public boolean isCorrect(int chosenIndex) {
        return chosenIndex == correctOptionIndex;
    }

    public String correctAnswerText() {
        return options.get(correctOptionIndex);
    }
}
