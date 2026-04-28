package com.goggles.lecture_service.application.enrollment.command.dto;

import java.util.List;
import java.util.UUID;

public record LectureEnrollmentReserveCommand(
    List<UUID> productIds, UUID userId, String userName) {}
