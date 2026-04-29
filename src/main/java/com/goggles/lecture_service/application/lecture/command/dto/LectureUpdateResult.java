package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;

public record LectureUpdateResult(UUID lectureId, LectureStatus status) {

	public static LectureUpdateResult from(Lecture lecture) {
		return new LectureUpdateResult(lecture.getId(), lecture.getStatus());
	}
}