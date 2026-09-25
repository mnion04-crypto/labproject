package com.banglalearn.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Owns the single SQLite connection for the app and makes sure the schema
 * exists. The DB file lives next to the app so progress persists between runs.
 */
public class DatabaseManager {

    private static final String DB_FILE_NAME = "bangla_progress.db";
    private static DatabaseManager instance;

    private final Connection connection;

    private DatabaseManager() throws SQLException {
        String url = "jdbc:sqlite:" + new File(DB_FILE_NAME).getAbsolutePath();
        this.connection = DriverManager.getConnection(url);
        initSchema();
    }

    public static synchronized DatabaseManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection connection() {
        return connection;
    }

    private void initSchema() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS profiles (
                    id   INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
                """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS lesson_progress (
                    id             INTEGER PRIMARY KEY AUTOINCREMENT,
                    profile_id     INTEGER NOT NULL,
                    lesson_group   TEXT NOT NULL,
                    score_percent  INTEGER NOT NULL,
                    perfect_score  INTEGER NOT NULL,
                    completed_at   TEXT NOT NULL,
                    FOREIGN KEY (profile_id) REFERENCES profiles(id)
                )
                """);
        }
    }
}
