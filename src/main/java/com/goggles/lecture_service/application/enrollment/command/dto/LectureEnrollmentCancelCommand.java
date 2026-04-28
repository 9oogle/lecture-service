package com.goggles.lecture_service.application.enrollment.command.dto;

import java.util.List;
import java.util.UUID;

public record LectureEnrollmentCancelCommand(List<UUID> enrollmentIds, UUID userId) {}
