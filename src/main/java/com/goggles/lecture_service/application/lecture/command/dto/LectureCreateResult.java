package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;

public record LectureCreateResult(UUID id, LectureStatus status) {

	public static LectureCreateResult from(Lecture lecture) {
		return new LectureCreateResult(lecture.getId(), lecture.getStatus());
	}
}