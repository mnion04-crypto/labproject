package com.banglalearn.lesson;

import com.banglalearn.model.LessonItem;

public class BanglaToRomanizationExercise extends Exercise {

    public BanglaToRomanizationExercise(LessonItem item) {
        super(item);
    }

    @Override
    public ExerciseType type() { return ExerciseType.BANGLA_TO_ROMANIZATION; }

    @Override
    public String prompt() { return sourceItem().bangla(); }

    @Override
    public String answerFor(LessonItem item) { return item.romanization(); }
}