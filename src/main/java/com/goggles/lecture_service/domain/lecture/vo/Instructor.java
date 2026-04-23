package com.goggles.lecture_service.domain.lecture.vo;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Instructor(
	@Column(name = "instructor_id", nullable = false)
	UUID id,
	@Column(name = "instructor_name", nullable = false, length = 100)
	String name
) {
	public Instructor {
		if (id == null)
			throw new IllegalArgumentException("강사 ID는 필수입니다.");
		if (name == null || name.isBlank())
			throw new IllegalArgumentException("강사 이름은 필수입니다.");
	}
}
