package com.goggles.lecture_service.domain.lecture.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ChapterDuration(
	@Column(name = "duration_seconds", nullable = false)
	int seconds
) {
	public ChapterDuration {
		if (seconds < 0) {
			throw new IllegalArgumentException("영상 길이는 0 이상이어야 합니다.");
		}
	}
}