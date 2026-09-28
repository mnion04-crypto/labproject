package com.banglalearn.db;

import com.banglalearn.model.BanglaWord;
import com.banglalearn.model.PartOfSpeech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** SQL for user-added words. Built-in words come from words.json and are read-only. */
public class WordDAO {

    private static final String ID_PREFIX = "custom_";

    private final Connection connection;

    public WordDAO() throws SQLException {
        this.connection = DatabaseManager.getInstance().connection();
    }

    /** True if this word was added by the user (so it can be edited/deleted). */
    public static boolean isCustom(BanglaWord word) {
        return word.id().startsWith(ID_PREFIX);
    }

    private static int rowId(String wordId) {
        return Integer.parseInt(wordId.substring(ID_PREFIX.length()));
    }

    public List<BanglaWord> getAll() throws SQLException {
        String sql = "SELECT id, bangla, romanization, english, part_of_speech, lesson_group "
                + "FROM custom_words ORDER BY id";
        List<BanglaWord> words = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                words.add(new BanglaWord(
                        ID_PREFIX + rs.getInt("id"),
                        rs.getString("bangla"),
                        rs.getString("romanization"),
                        rs.getString("english"),
                        PartOfSpeech.valueOf(rs.getString("part_of_speech")),
                        rs.getString("lesson_group"),
                        1));
            }
        }
        return words;
    }

    public BanglaWord add(String bangla, String romanization, String english,
                          PartOfSpeech pos, String group) throws SQLException {
        String sql = "INSERT INTO custom_words(bangla, romanization, english, part_of_speech, lesson_group) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bangla);
            ps.setString(2, romanization);
            ps.setString(3, english);
            ps.setString(4, pos.name());
            ps.setString(5, group);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new BanglaWord(ID_PREFIX + keys.getInt(1), bangla, romanization, english, pos, group, 1);
            }
        }
    }

    public void update(BanglaWord word) throws SQLException {
        String sql = "UPDATE custom_words SET bangla = ?, romanization = ?, english = ?, "
                + "part_of_speech = ?, lesson_group = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, word.bangla());
            ps.setString(2, word.romanization());
            ps.setString(3, word.englishMeaning());
            ps.setString(4, word.partOfSpeech().name());
            ps.setString(5, word.lessonGroup());
            ps.setInt(6, rowId(word.id()));
            ps.executeUpdate();
        }
    }

    public void delete(String wordId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM custom_words WHERE id = ?")) {
            ps.setInt(1, rowId(wordId));
            ps.executeUpdate();
        }
    }
}