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

	// 필요 시 UI를 위한 편의 메서드 추가 가능
	public String toFormattedTime() {
		long h = seconds / 3600;
		long m = (seconds % 3600) / 60;
		long s = seconds % 60;
		return h > 0 ? String.format("%02d:%02d:%02d", h, m, s) : String.format("%02d:%02d", m, s);
	}
}