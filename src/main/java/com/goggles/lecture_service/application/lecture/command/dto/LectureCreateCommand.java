package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;

public record LectureCreateCommand(
	UUID instructorId,
	String instructorName,
	String category,
	String title,
	String subtitle,
	String description,
	DurationPolicy durationPolicy,
	Long price) {
}
