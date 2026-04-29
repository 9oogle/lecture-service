package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.ForbiddenException;

public class LectureAccessDeniedException extends ForbiddenException {

	public LectureAccessDeniedException() {
		super(LectureErrorCode.LECTURE_ACCESS_DENIED.getMessage());
	}
}