package com.banglalearn.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * All SQL for profiles and lesson_progress lives here. Every method is a
 * plain blocking JDBC call; callers (see util.BackgroundTasks) are expected
 * to run these off the JavaFX Application Thread.
 */
public class ProgressDAO {

    private final Connection connection;

    public ProgressDAO() throws SQLException {
        this.connection = DatabaseManager.getInstance().connection();
    }

    // ---------- Profiles ----------

    public List<UserProfile> getAllProfiles() throws SQLException {
        String sql = "SELECT id, name FROM profiles ORDER BY name";
        List<UserProfile> profiles = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                profiles.add(new UserProfile(rs.getInt("id"), rs.getString("name")));
            }
        }
        return profiles;
    }

    public UserProfile createProfile(String name) throws SQLException {
        String sql = "INSERT INTO profiles(name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new UserProfile(keys.getInt(1), name);
            }
        }
    }

    public void deleteProfile(int profileId) throws SQLException {
        try (PreparedStatement ps1 = connection.prepareStatement(
                "DELETE FROM lesson_progress WHERE profile_id = ?")) {
            ps1.setInt(1, profileId);
            ps1.executeUpdate();
        }
        try (PreparedStatement ps2 = connection.prepareStatement(
                "DELETE FROM profiles WHERE id = ?")) {
            ps2.setInt(1, profileId);
            ps2.executeUpdate();
        }
    }

    // ---------- Lesson progress ----------

    public void recordLessonCompletion(int profileId, String lessonGroup,
                                        int scorePercent, boolean perfectScore) throws SQLException {
        String sql = """
            INSERT INTO lesson_progress(profile_id, lesson_group, score_percent, perfect_score, completed_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, profileId);
            ps.setString(2, lessonGroup);
            ps.setInt(3, scorePercent);
            ps.setInt(4, perfectScore ? 1 : 0);
            ps.setString(5, LocalDateTime.now().toString());
            ps.executeUpdate();
        }
    }

    public List<LessonProgress> getProgressForProfile(int profileId) throws SQLException {
        String sql = """
            SELECT id, profile_id, lesson_group, score_percent, perfect_score, completed_at
            FROM lesson_progress WHERE profile_id = ? ORDER BY completed_at DESC
            """;
        List<LessonProgress> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new LessonProgress(
                            rs.getInt("id"),
                            rs.getInt("profile_id"),
                            rs.getString("lesson_group"),
                            rs.getInt("score_percent"),
                            rs.getInt("perfect_score") == 1,
                            LocalDateTime.parse(rs.getString("completed_at"))
                    ));
                }
            }
        }
        return results;
    }

    /** Best score achieved for a given lesson group, if attempted before. */
    public Optional<Integer> bestScoreFor(int profileId, String lessonGroup) throws SQLException {
        String sql = """
            SELECT MAX(score_percent) AS best FROM lesson_progress
            WHERE profile_id = ? AND lesson_group = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, profileId);
            ps.setString(2, lessonGroup);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getObject("best") != null) {
                    return Optional.of(rs.getInt("best"));
                }
                return Optional.empty();
            }
        }
    }

    public boolean hasPerfectScore(int profileId, String lessonGroup) throws SQLException {
        return bestScoreFor(profileId, lessonGroup).map(score -> score == 100).orElse(false);
    }
}
