package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.NotFoundException;
import java.util.UUID;

public class ChapterNotFoundException extends NotFoundException {
	public ChapterNotFoundException(UUID id) {
		super(LectureErrorCode.CHAPTER_NOT_FOUND.getMessage() + " id=" + id);
	}
}