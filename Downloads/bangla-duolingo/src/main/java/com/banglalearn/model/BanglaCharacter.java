package com.banglalearn.model;

/**
 * One character of the Bangla script (a vowel or consonant).
 *
 * @param id             unique id, e.g. "char_o"
 * @param bangla         the glyph itself, e.g. "অ"
 * @param romanization   Latin transliteration, e.g. "o"
 * @param englishMeaning short descriptive name shown in quizzes, e.g. "vowel O"
 * @param type           vowel or consonant
 * @param exampleWord    a Bangla word starting with/featuring this character, for context
 * @param difficulty     1 = taught first, higher = taught later
 */
public record BanglaCharacter(
        String id,
        String bangla,
        String romanization,
        String englishMeaning,
        CharacterType type,
        String exampleWord,
        int difficulty
) implements LessonItem {

    @Override
    public String lessonGroup() {
        return type == CharacterType.VOWEL ? "vowels" : "consonants";
    }
}
