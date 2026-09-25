package com.banglalearn.lesson;

import com.banglalearn.model.LessonItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Turns any List&lt;LessonItem&gt; into a list of multiple-choice Exercises,
 * with randomized wrong-answer distractors pulled from the rest of the pool.
 *
 * Because it only depends on the LessonItem interface, this class works
 * unchanged for BanglaCharacter, BanglaWord, or any content type added later.
 */
public class LessonFactory {

    private static final int DEFAULT_OPTION_COUNT = 4;
    private final Random random;

    public LessonFactory() {
        this(new Random());
    }

    /** Package-visible constructor for deterministic tests. */
    LessonFactory(Random random) {
        this.random = random;
    }

    /**
     * Builds one exercise per item in {@code items}, each using the given
     * {@code type}, with distractors drawn from {@code pool} (usually the
     * same list, or a wider pool for small lesson groups).
     */
    public List<Exercise> buildLesson(List<? extends LessonItem> items,
                                       List<? extends LessonItem> pool,
                                       ExerciseType type) {
        List<Exercise> exercises = new ArrayList<>();
        for (LessonItem item : items) {
            exercises.add(buildExercise(item, pool, type));
        }
        return exercises;
    }

    /** Convenience overload: distractors drawn from the same list being taught. */
    public List<Exercise> buildLesson(List<? extends LessonItem> items, ExerciseType type) {
        return buildLesson(items, items, type);
    }

    /** Builds a "teach one at a time, then quiz" flow: returns items followed by their exercises. */
    public List<Exercise> buildTeachThenQuiz(List<? extends LessonItem> items, ExerciseType type) {
        return buildLesson(items, type);
    }

    public Exercise buildExercise(LessonItem item, List<? extends LessonItem> pool, ExerciseType type) {
        String prompt = switch (type) {
            case BANGLA_TO_ENGLISH -> item.bangla();
            case ENGLISH_TO_BANGLA -> item.englishMeaning();
            case BANGLA_TO_ROMANIZATION -> item.bangla();
        };

        String correctAnswer = switch (type) {
            case BANGLA_TO_ENGLISH -> item.englishMeaning();
            case ENGLISH_TO_BANGLA -> item.bangla();
            case BANGLA_TO_ROMANIZATION -> item.romanization();
        };

        List<String> options = generateOptions(item, pool, type, correctAnswer);
        int correctIndex = options.indexOf(correctAnswer);

        return new Exercise(item, type, prompt, options, correctIndex);
    }

    private List<String> generateOptions(LessonItem correctItem, List<? extends LessonItem> pool,
                                          ExerciseType type, String correctAnswer) {
        List<String> distractorPool = new ArrayList<>();
        for (LessonItem candidate : pool) {
            if (candidate.id().equals(correctItem.id())) continue;
            String candidateAnswer = switch (type) {
                case BANGLA_TO_ENGLISH -> candidate.englishMeaning();
                case ENGLISH_TO_BANGLA -> candidate.bangla();
                case BANGLA_TO_ROMANIZATION -> candidate.romanization();
            };
            if (!candidateAnswer.equals(correctAnswer) && !distractorPool.contains(candidateAnswer)) {
                distractorPool.add(candidateAnswer);
            }
        }
        Collections.shuffle(distractorPool, random);

        int wrongNeeded = Math.min(DEFAULT_OPTION_COUNT - 1, distractorPool.size());
        List<String> options = new ArrayList<>(distractorPool.subList(0, wrongNeeded));
        options.add(correctAnswer);
        Collections.shuffle(options, random);
        return options;
    }
}
