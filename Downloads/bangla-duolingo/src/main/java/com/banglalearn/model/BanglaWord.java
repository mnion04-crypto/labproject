package com.banglalearn.model;

/**
 * One vocabulary word or phrase.
 *
 * @param id             unique id, e.g. "word_jol"
 * @param bangla         the word in Bangla script, e.g. "জল"
 * @param romanization   transliteration, e.g. "jol"
 * @param englishMeaning translation, e.g. "water"
 * @param partOfSpeech   grammatical category
 * @param lessonGroup    topical group, e.g. "greetings", "food", "family"
 * @param difficulty     1 = taught first, higher = taught later
 */
public record BanglaWord(
        String id,
        String bangla,
        String romanization,
        String englishMeaning,
        PartOfSpeech partOfSpeech,
        String lessonGroup,
        int difficulty
) implements LessonItem {
}
