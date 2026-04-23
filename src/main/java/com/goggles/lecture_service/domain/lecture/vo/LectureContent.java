package com.goggles.lecture_service.domain.lecture.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record LectureContent(
	@Column(nullable = false, length = 200)
	String title,
	@Column(length = 300)
	String subtitle,
	@Column(columnDefinition = "TEXT")
	String description
) {
	public LectureContent {
		if (title == null || title.isBlank()) throw new IllegalArgumentException("강의 제목은 필수입니다.");
	}
}