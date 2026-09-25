package com.banglalearn.model;

/** Bangla script is organized into vowels (স্বরবর্ণ) and consonants (ব্যঞ্জনবর্ণ). */
public enum CharacterType {
    VOWEL("স্বরবর্ণ"),
    CONSONANT("ব্যঞ্জনবর্ণ");

    private final String banglaLabel;

    CharacterType(String banglaLabel) {
        this.banglaLabel = banglaLabel;
    }

    public String banglaLabel() {
        return banglaLabel;
    }
}
