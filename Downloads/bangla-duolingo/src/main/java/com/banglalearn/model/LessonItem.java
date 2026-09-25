package com.banglalearn.model;

/**
 * Common contract for anything that can be taught and quizzed:
 * Bangla characters, vocabulary words, and any future content type
 * (e.g. numbers, phrases) as the app grows.
 *
 * The lesson/quiz engine (see LessonFactory) only ever talks to
 * this interface, so adding a new content type never requires
 * touching the quiz-generation code.
 */
public interface LessonItem {

    /** Stable unique id, e.g. "char_ka" or "word_jol". Used as a DB key. */
    String id();

    /** The Bangla script glyph(s) shown as the prompt, e.g. "ক" or "জল". */
    String bangla();

    /** Latin transliteration, e.g. "ka" or "jol". */
    String romanization();

    /** English meaning / translation, used as the correct-answer text. */
    String englishMeaning();

    /** Which lesson group this belongs to (e.g. "vowels", "greetings"). */
    String lessonGroup();

    /** Relative difficulty, used to order items within a lesson. */
    int difficulty();
}
