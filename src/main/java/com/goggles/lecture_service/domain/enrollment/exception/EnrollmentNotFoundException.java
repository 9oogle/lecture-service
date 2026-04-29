package com.goggles.lecture_service.domain.enrollment.exception;

import java.util.UUID;

import com.goggles.common.exception.NotFoundException;

public class EnrollmentNotFoundException extends NotFoundException {

	public EnrollmentNotFoundException(UUID id) {
		super(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND.getMessage() + " id=" + id);
	}
}