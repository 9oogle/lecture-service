package com.goggles.lecture_service.domain.lecture.exception;

import java.util.UUID;

import com.goggles.common.exception.BadRequestException;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;

public class InvalidLectureStatusException extends BadRequestException {

	public InvalidLectureStatusException(UUID lectureId, LectureStatus status) {
		super("수정 가능한 강의 상태가 아닙니다. lectureId=" + lectureId + ", status=" + status);
	}
}
