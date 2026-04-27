package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

public record ChapterCreateResult(
	UUID lectureId,
	UUID chapterId
) {

	public static ChapterCreateResult from(UUID lectureId, UUID chapterId) {
		return new ChapterCreateResult(lectureId, chapterId);
	}
}