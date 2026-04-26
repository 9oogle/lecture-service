package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import java.util.UUID;

public record LectureCreateCommand(
    UUID instructorId,
    String instructorName,
    String category,
    String title,
    String subtitle,
    String description,
    DurationPolicy durationPolicy,
    Long price) {}
