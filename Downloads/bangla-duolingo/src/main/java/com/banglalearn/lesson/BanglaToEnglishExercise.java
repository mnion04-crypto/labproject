package com.banglalearn.lesson;

import com.banglalearn.model.LessonItem;

public class BanglaToEnglishExercise extends Exercise {

    public BanglaToEnglishExercise(LessonItem item) {
        super(item);
    }

    @Override
    public ExerciseType type() { return ExerciseType.BANGLA_TO_ENGLISH; }

    @Override
    public String prompt() { return sourceItem().bangla(); }

    @Override
    public String answerFor(LessonItem item) { return item.englishMeaning(); }
}