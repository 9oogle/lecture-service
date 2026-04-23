package com.goggles.lecture_service.domain.lecture.vo;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public record Money(
	@Column(name = "price", nullable = false)
	Long amount
) {
	public Money {
		// Long은 객체이므로 null 체크 후, 기본 연산자로 크기 비교 (오토 언박싱)
		if (amount == null || amount < 0L) {
			throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
		}
	}

	public static Money of(Long amount) {
		return new Money(amount);
	}
}