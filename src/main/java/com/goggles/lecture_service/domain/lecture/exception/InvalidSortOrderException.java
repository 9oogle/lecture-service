package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidSortOrderException extends BadRequestException {
	public InvalidSortOrderException(int sortOrder) {
		super(LectureErrorCode.INVALID_SORT_ORDER.getMessage() + " sortOrder=" + sortOrder);
	}
}