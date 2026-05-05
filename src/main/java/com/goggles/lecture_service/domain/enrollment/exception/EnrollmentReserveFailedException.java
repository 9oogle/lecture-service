package com.goggles.lecture_service.domain.enrollment.exception;

import com.goggles.common.exception.BadRequestException;
import com.goggles.lecture_service.domain.enrollment.enums.ReserveFailReason;
import java.util.UUID;
import lombok.Getter;

@Getter
public class EnrollmentReserveFailedException extends BadRequestException {

  private final UUID failedProductId;
  private final ReserveFailReason reason;

  public EnrollmentReserveFailedException(UUID failedProductId, ReserveFailReason reason) {
    super(failedProductId.toString(), reason.name());
    this.failedProductId = failedProductId;
    this.reason = reason;
  }
}
