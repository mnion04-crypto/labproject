package com.banglalearn.lesson;

import com.banglalearn.model.LessonItem;

public class EnglishToBanglaExercise extends Exercise {

    public EnglishToBanglaExercise(LessonItem item) {
        super(item);
    }

    @Override
    public ExerciseType type() { return ExerciseType.ENGLISH_TO_BANGLA; }

    @Override
    public String prompt() { return sourceItem().englishMeaning(); }

    @Override
    public String answerFor(LessonItem item) { return item.bangla(); }
}