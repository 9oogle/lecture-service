package com.goggles.lecture_service.domain.enrollment.exception;

import java.util.UUID;

import com.goggles.common.exception.BadRequestException;
import com.goggles.lecture_service.domain.enrollment.enums.ReserveFailReason;

import lombok.Getter;

@Getter
public class EnrollmentReserveFailedException extends BadRequestException {

	private final UUID failedProductId;
	private final ReserveFailReason reason;

	public EnrollmentReserveFailedException(UUID failedProductId, ReserveFailReason reason) {
		super(
			String.format(
				"수강 등록 예약 실패 - productId: %s, reason: %s", failedProductId, reason.name()));
		this.failedProductId = failedProductId;
		this.reason = reason;
	}
}