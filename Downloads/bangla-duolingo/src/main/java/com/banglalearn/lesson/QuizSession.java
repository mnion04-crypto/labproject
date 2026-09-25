package com.banglalearn.lesson;

import java.util.List;

/**
 * Walks through a fixed list of Exercises one at a time and tracks the score.
 * The UI layer (LessonController) drives this; it holds no JavaFX state.
 */
public class QuizSession {

    private final String lessonGroup;
    private final List<Exercise> exercises;
    private int currentIndex = 0;
    private int correctCount = 0;

    public QuizSession(String lessonGroup, List<Exercise> exercises) {
        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("A quiz session needs at least one exercise");
        }
        this.lessonGroup = lessonGroup;
        this.exercises = exercises;
    }

    public String lessonGroup() {
        return lessonGroup;
    }

    public Exercise currentExercise() {
        return exercises.get(currentIndex);
    }

    public int currentIndexOneBased() {
        return currentIndex + 1;
    }

    public int totalExercises() {
        return exercises.size();
    }

    public boolean isFinished() {
        return currentIndex >= exercises.size();
    }

    /** Records the user's answer for the current exercise and advances. Returns whether it was correct. */
    public boolean submitAnswer(int chosenOptionIndex) {
        boolean correct = currentExercise().isCorrect(chosenOptionIndex);
        if (correct) correctCount++;
        currentIndex++;
        return correct;
    }

    public int correctCount() {
        return correctCount;
    }

    public int scorePercent() {
        return (int) Math.round(100.0 * correctCount / exercises.size());
    }

    public boolean isPerfectScore() {
        return correctCount == exercises.size();
    }
}
