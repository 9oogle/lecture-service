package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

public record LectureDeleteResult(UUID lectureId) {

	public static LectureDeleteResult from(UUID lectureId) {
		return new LectureDeleteResult(lectureId);
	}
}