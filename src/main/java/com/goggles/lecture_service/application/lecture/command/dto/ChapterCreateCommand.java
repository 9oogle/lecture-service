package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

public record ChapterCreateCommand(
    UUID lectureId, String title, String content, int sortOrder, int durationSeconds) {}
