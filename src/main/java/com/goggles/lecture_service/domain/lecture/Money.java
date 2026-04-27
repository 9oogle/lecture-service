package com.goggles.lecture_service.domain.lecture;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Money {

  @Column(name = "price", nullable = false)
  private Long amount;

  protected Money() {}

  // Money.java
  private Money(Long amount) {
    if (amount == null || amount < 0L) {
      throw new InvalidLectureFieldException(LectureErrorCode.PRICE_NEGATIVE);
    }
    this.amount = amount;
  }

  public static Money of(Long amount) {
    return new Money(amount);
  }
}
