package com.banglalearn.db;

import java.time.LocalDateTime;

public record LessonProgress(
        int id,
        int profileId,
        String lessonGroup,
        int scorePercent,
        boolean perfectScore,
        LocalDateTime completedAt
) {
}
