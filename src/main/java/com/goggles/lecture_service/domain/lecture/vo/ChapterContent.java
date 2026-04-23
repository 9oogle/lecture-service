package com.goggles.lecture_service.domain.lecture.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ChapterContent(
	@Column(nullable = false, length = 200)
	String title,

	@Column(columnDefinition = "TEXT")
	String content
) {
	public ChapterContent {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("챕터 제목은 필수입니다.");
		}
	}
}
