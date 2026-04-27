package com.goggles.lecture_service.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Money {

  @Column(name = "price", nullable = false)
  private Long amount;

  protected Money() {}

  private Money(Long amount) {
    if (amount == null || amount < 0L) {
      throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
    }
    this.amount = amount;
  }

  public static Money of(Long amount) {
    return new Money(amount);
  }

  public Long getAmount() {
    return amount;
  }
}
