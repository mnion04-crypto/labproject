package com.banglalearn.data;

import com.banglalearn.model.BanglaCharacter;
import com.banglalearn.model.BanglaWord;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Reads the bundled JSON content files and parses them into model objects.
 * Pure I/O + parsing, no JavaFX types here — this class is safe to call
 * from a background thread (see util.BackgroundTasks / ContentLoadTask).
 */
public class ContentLoader {

    private static final String CHARACTERS_PATH = "/data/characters.json";
    private static final String WORDS_PATH = "/data/words.json";

    private final ObjectMapper mapper = new ObjectMapper();

    public List<BanglaCharacter> loadCharacters() throws IOException {
        try (InputStream in = getClass().getResourceAsStream(CHARACTERS_PATH)) {
            if (in == null) {
                throw new IOException("Missing resource: " + CHARACTERS_PATH);
            }
            return mapper.readValue(in, mapper.getTypeFactory()
                    .constructCollectionType(List.class, BanglaCharacter.class));
        }
    }

    public List<BanglaWord> loadWords() throws IOException {
        try (InputStream in = getClass().getResourceAsStream(WORDS_PATH)) {
            if (in == null) {
                throw new IOException("Missing resource: " + WORDS_PATH);
            }
            return mapper.readValue(in, mapper.getTypeFactory()
                    .constructCollectionType(List.class, BanglaWord.class));
        }
    }
}
